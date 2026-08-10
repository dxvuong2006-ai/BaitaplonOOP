package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.factory.storage.TransactionStorageFactory;
import com.expensemanager.factory.model.TransactionFactory;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.enums.FilePath;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.repository.Storage;
import com.expensemanager.service.BudgetService;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.transaction.TransactionRecord;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.RecurringExpense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Quản lý giao dịch. */
public class TransactionService {

    private List<Transaction> transactions = new ArrayList<>();
    private TransactionStorageFactory storageFactory;
    private BudgetService budgetService;
    private WalletService walletService;
    private CategoryService categoryService;

    public TransactionService(BudgetService budgetService, WalletService walletService,
                            CategoryService categoryService, TransactionStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
        this.budgetService = budgetService;
        this.walletService = walletService;
        this.categoryService = categoryService;
        load();
    }

    public void load() {
        transactions.clear();;
        List<TransactionRecord> records = storageFactory.load(FilePath.TRANSACTION);
        for (TransactionRecord record : records) {
            try {
                transactions.add(toTransaction(record));
            } catch (Exception e) {
                System.err.println("Bỏ qua giao dịch id = " + record.getId()
                                    + " do lỗi " + e.getMessage());
            }
        }
    }

    public void save() {
        List<TransactionRecord> records = new ArrayList<>();
        for (Transaction transaction : transactions) {
            records.add(toRecord(transaction));
        }
        storageFactory.save(FilePath.TRANSACTION, records);
    }

    /** Chuyển toàn bộ giao dịch thành dạng dữ liệu thô. */
    private Transaction toTransaction(TransactionRecord record) {
        Category category = categoryService.findCategoryById(record.getCategoryId(),
                record.getUserId());
        ValidationService.validateCategory(category);

        Wallet wallet = walletService.findWalletById(record.getWalletId(),
                record.getUserId());
        ValidationService.validateWallet(wallet);

        TransactionType type = TransactionType.valueOf(record.getType());

        String source = type == TransactionType.INCOME ? record.getExtraField() : null;
        String paymentMethod = type != TransactionType.INCOME ? record.getExtraField() : null;
        Period period = (record.getPeriod() != null && !record.getPeriod().isBlank())
                ? Period.valueOf(record.getPeriod())
                : null;

        return TransactionFactory.createTransaction(
                type,
                record.getId(),
                record.getAmount(),
                record.getDate(),
                record.getNote(),
                category,
                wallet,
                source,
                paymentMethod,
                period,
                record.getUserId()
        );
    }

    /** Chuyển dữ liệu thô thành danh sách giao dịch. */
    private TransactionRecord toRecord(Transaction transaction) {
        return new TransactionRecord(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getNote(),
                transaction.getCategory() == null ? "" : transaction.getCategory().getId(),
                transaction.getWallet() == null ? "" : transaction.getWallet().getId(),
                transaction.getType().name(),
                extractExtraField(transaction),
                extractPeriod(transaction),
                transaction.getUserId()
        );
    }

    /** Trích xuất trường phụ: source (của Income) hoặc paymentMethod (của Expense/RecurringExpense). */
    private String extractExtraField(Transaction tx) {
        if (tx instanceof Income) {
            return ((Income) tx).getSource();
        }
        if (tx instanceof Expense) {
            return ((Expense) tx).getPaymentMethod();
        }
        return "";
    }

    /** Trích xuất chu kỳ: Chỉ RecurringExpense mới có Period. */
    private String extractPeriod(Transaction tx) {
        if (tx instanceof RecurringExpense) {
            return ((RecurringExpense) tx).getPeriod().name();
        }
        return "";
    }

    /** Thêm giao dịch. */
    public void addTransaction(Transaction transaction, int userId) {
        ValidationService.validateTransaction(transaction);
        if (findTransactionById(transaction.getId(), userId) != null) {
            throw new DuplicateEntityException("Giao dịch", "mã " + transaction.getId());
        }
        ValidationService.validateWallet(transaction.getWallet());
        Wallet wallet = transaction.getWallet();
        double signedAmount = transaction.getSignedAmount();
        if (signedAmount < 0) {
            ValidationService.validateWithdraw(wallet, -signedAmount);
            Budget budget = budgetService.findBudgetByCategory(transaction.getCategory(), userId);
            if (budget != null) {
                budgetService.validateBudgetLimit(budget, -signedAmount, userId);
            }
            wallet.withdraw(-signedAmount);
        } else {
            wallet.deposit(signedAmount);
        }
        walletService.save();
        transactions.add(transaction);
        save();
    }

    /** Xóa giao dịch. */
    public void removeTransaction(Transaction transaction, int userId) {
        ValidationService.validateTransaction(transaction);
        if (!transactions.contains(transaction)) {
            return;
        }
        Wallet wallet = transaction.getWallet();
        double signedAmount = transaction.getSignedAmount();
        if (signedAmount < 0) {
            wallet.deposit(-signedAmount);
        } else {
            ValidationService.validateWithdraw(wallet, signedAmount);
            wallet.withdraw(signedAmount);
        }
        walletService.save();
        transactions.remove(findTransactionById(transaction.getId(), userId));
        save();
    }

    /** Chỉnh sửa lại giao dịch. */
    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction, int userId) {
        ValidationService.validateTransaction(oldTransaction);
        ValidationService.validateTransaction(newTransaction);
        removeTransaction(oldTransaction, userId);
        try {
            addTransaction(newTransaction, userId);
        } catch (RuntimeException e) {
            addTransaction(oldTransaction, userId);
            throw e;
        }
        save();
    }

    /** Tìm giao dịch bằng ID. */
    public Transaction findTransactionById(String id, int userId) {
        if (id == null || id.trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        for (Transaction transaction : getTransactions(userId)) {
            if (transaction.getId().equals(id) && transaction.getUserId() == userId) {
                return transaction;
            }
        }
        return null;
    }

    /** Tìm giao dịch theo ví. */
    public List<Transaction> findTransactionByWallet(Wallet wallet, int userId) {
        ValidationService.validateWallet(wallet);
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : getTransactions(userId)) {
            if (wallet.equals(transaction.getWallet())) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Tìm giao dịch theo danh mục. */
    public List<Transaction> findTransactionByCategory(Category category, int userId) {
        ValidationService.validateCategory(category);
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : getTransactions(userId)) {
            if (category.equals(transaction.getCategory())) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Tìm giao dịch theo loại. */
    public List<Transaction> findTransactionByType(TransactionType type, int userId) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.TRANSACTIONTYPE);
        }
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : getTransactions(userId)) {
            if (type.equals(transaction.getType())) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Trả về danh sách các giao dịch. */
    public List<Transaction> getTransactions(int userId) {
        return transactions.stream()
                .filter(transaction -> transaction.getUserId() == userId)
                .toList();
    }
}
