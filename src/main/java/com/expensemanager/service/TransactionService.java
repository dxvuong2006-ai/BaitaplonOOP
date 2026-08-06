package com.expensemanager.service;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.service.BudgetService;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import org.apache.poi.ss.formula.functions.T;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Quản lý giao dịch. */
public class TransactionService {

    private List<Transaction> transactions;
    private BudgetService budgetService;

    public TransactionService(BudgetService budgetService) {
        this.transactions = new ArrayList<>();
        this.budgetService = budgetService;
    }

    /** Thêm giao dịch. */
    public void addTransaction(Transaction transaction) {
        ValidationService.validateTransaction(transaction);
        ValidationService.validateWallet(transaction.getWallet());
        Wallet wallet = transaction.getWallet();
        double signedAmount = transaction.getSignedAmount();
        if (signedAmount < 0) {
            ValidationService.validateWithdraw(wallet, -signedAmount);
            wallet.withdraw(-signedAmount);
            Budget budget = budgetService.findBudgetByCategory(transaction.getCategory());
            if (budget != null) {
                budgetService.validateBudgetLimit(budget);
            }
        } else {
            wallet.deposit(signedAmount);
        }
        transactions.add(transaction);
    }

    /** Xóa giao dịch. */
    public void removeTransaction(Transaction transaction) {
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
        transactions.remove(transaction);
    }

    /** Chỉnh sửa lại giao dịch. */
    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        ValidationService.validateTransaction(oldTransaction);
        ValidationService.validateTransaction(newTransaction);
        removeTransaction(oldTransaction);
        try {
            addTransaction(newTransaction);
        } catch (RuntimeException e) {
            addTransaction(oldTransaction);
            throw e;
        }
    }

    /** Tìm giao dịch bằng ID. */
    public Transaction findTransactionById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(id)) {
                return transaction;
            }
        }
        return null;
    }

    /** Tìm giao dịch theo ví. */
    public List<Transaction> findTransactionByWallet(Wallet wallet) {
        ValidationService.validateWallet(wallet);
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (wallet.equals(transaction.getWallet())) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Tìm giao dịch theo danh mục. */
    public List<Transaction> findTransactionByCategory(Category category) {
        ValidationService.validateCategory(category);
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (category.equals(transaction.getCategory()))) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Tìm giao dịch theo loại. */
    public List<Transaction> findTransactionByType(TransactionType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.TRANSACTIONTYPE);
        }
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (type.equals(transaction.getType())) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Trả về danh sách các giao dịch. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
