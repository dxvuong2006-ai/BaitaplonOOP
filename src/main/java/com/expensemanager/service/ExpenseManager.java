package com.expensemanager.service;

import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.model.enums.FieldType;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.exception.InsufficientFundsException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Lớp xử lý nghiệp vụ của dự án. */
public class ExpenseManager {
    private static ExpenseManager instance;

    private final List<Transaction> transactions;
    private final List<Wallet> wallets;
    private final List<Category> categories;
    private final List<Budget> budgets;

    private final StatisticsService statisticsService;
    private final BudgetService budgetService;
    private final ReportService reportService;

    /** Phương thức khởi tạo của EM. */
    private ExpenseManager() {
        transactions = new ArrayList<>();
        wallets = new ArrayList<>();
        categories = new ArrayList<>();
        budgets = new ArrayList<>();

        statisticsService = new StatisticsService();
        budgetService = new BudgetService();
        reportService = new ReportService();
    }

    /** Giúp lấy object. */
    public static ExpenseManager getInstance() {
        if (instance == null) {
            instance = new ExpenseManager();
        }
        return instance;
    }

    /** Tạo thêm 1 giao dịch. */
    public void addTransaction(Transaction transaction) {
        if (transaction == null || transaction.getWallet() == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        Wallet wallet = transaction.getWallet();
        if (wallet == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        double signedAmount = transaction.getSignedAmount();
        if (signedAmount < 0) {
            ValidationService.validateWithdraw(wallet, -signedAmount);
            wallet.withdraw(-signedAmount);
            Budget budget = findBudgetByCategory(transaction.getCategory());
            if (budget != null) {
                budgetService.validateBudgetLimit(budget);
            }
        } else {
            wallet.deposit(signedAmount);
        }
        transactions.add(transaction);
    }

    /** Trả về danh sách các giao dịch. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /** Xóa giao dịch. */
    public void removeTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new EmptyFieldException(FieldType.ID);
        }
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
        transactions.remove(transaction);
    }

    /** Tìm giao dịch bằng ID. */
    public Transaction findTransactionById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(id)) {
                return transaction;
            }
        }
        return null;
    }

    /** Tạo loại ví mới. */
    public void addWallet (Wallet wallet) {
        if (wallet == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        ValidationService
                .validateWalletName(wallets, wallet.getName());
        wallets.add(wallet);
    }

    /** Xóa ví. */
    public void removeWallet(Wallet wallet) {
        if (wallet == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        wallets.remove(wallet);
    }

    /** Tìm kiếm ví bằng tên. */
    public Wallet findWalletByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Wallet wallet : wallets) {
            if (wallet.getName().equalsIgnoreCase(name.trim())) {
                return wallet;
            }
        }
        return null;
    }

    /** Trả về danh sách ví. */
    public List<Wallet> getWallets() {
        return Collections.unmodifiableList(wallets);
    }

    /** Giúp hiển thị số tiền dạng chuẩn. */
    public String getFormattedBalance(Wallet wallet) {
        return CurrencyUtils.formatVND(wallet.getBalance());
    }

    /** Thêm loại. */
    public void addCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        if (findCategoryByName(category.getName()) != null) {
            throw new DuplicateEntityException("Danh mục", category.getName());
        }
        categories.add(category);
    }

    /** Xóa loại. */
    public void removeCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        categories.remove(category);
    }

    /** Tìm kiếm loại theo tên. */
    public Category findCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name.trim())) {
                return category;
            }
        }
        return null;
    }

    /** Trả về danh sách loại. */
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /** Thêm ngân sách. */
    public void addBudget(Budget budget) {
        if (budget == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        budgets.add(budget);
    }

    /** Xóa ngân sách. */
    public void removeBudget(Budget budget) {
        if (budget == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        budgets.remove(budget);
    }

    /** Tìm kiếm ngân sách bằng loại. */
    public Budget findBudgetByCategory(Category category) {
        if (category == null) {
            return null;
        }
        for (Budget budget : budgets) {
            if (Objects.equals(budget.getCategory(), category)) {
                return budget;
            }
        }
        return null;
    }

    /** Danh sách các loại ngân sách. */
    public List<Budget> getBudgets() {
        return Collections.unmodifiableList(budgets);
    }
}
