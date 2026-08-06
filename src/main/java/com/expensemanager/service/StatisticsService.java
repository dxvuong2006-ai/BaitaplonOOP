package com.expensemanager.service;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/** Lớp cung cấp chức năng thống kê giao dịch. */
public class StatisticsService {

    /** Lấy danh sách giao dịch trong khoản thời gian. */
    public List<Transaction> getTransactionsInPeriod(List<Transaction> transactions,
                                                     LocalDate startDate, LocalDate endDate) {
        ValidationService.validateTransactions(transactions);
        ValidationService.validateDateRange(startDate, endDate);
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (!transaction.getDate().isBefore(startDate)
                    && !transaction.getDate().isAfter(endDate)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /** Tổng thu. */
    public double calculateTotalIncome(List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() == TransactionType.INCOME) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    /** Tổng chi. */
    public double calculateTotalExpense(List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() == TransactionType.EXPENSE) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    /** Chênh lệch giữa tổng thu và tổng chi. */
    public double calculateNetSaving(List<Transaction> transactions) {
        return calculateTotalIncome(transactions) - calculateTotalExpense(transactions);
    }

    /** Thống kê chi theo từng loại giao dịch. */
    public Map<Category, Double> calculateExpenseByCategory(List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        Map<Category, Double> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() != TransactionType.EXPENSE) {
                continue;
            }
            Category category = transaction.getCategory();
            if (result.containsKey(category)) {
                result.put(category,
                        result.get(category) + transaction.getAmount());
            } else {
                result.put(category, transaction.getAmount());
            }
        }
        return result;
    }

    /** Thống kê thu theo từng loại giao dịch. */
    public Map<Category, Double> calculateIncomeByCategory(
            List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        Map<Category, Double> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() != TransactionType.INCOME) {
                continue;
            }
            Category category = transaction.getCategory();
            if (result.containsKey(category)) {
                result.put(category,
                        result.get(category) + transaction.getAmount());
            } else {
                result.put(category, transaction.getAmount());
            }
        }
        return result;
    }

    /** Đếm số giao dịch. */
    public int countTransactions(List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        int count = 0;
        for (Transaction transaction : transactions) {
            if (transaction != null) {
                count++;
            }
        }
        return count;
    }

    /** Thống kê chi theo loại ví. */
    public Map<Wallet, Double> calculateExpenseByWallet(List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        Map<Wallet, Double> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() != TransactionType.EXPENSE) {
                continue;
            }
            Wallet wallet = transaction.getWallet();
            if (result.containsKey(wallet)) {
                result.put(wallet,
                        result.get(wallet) + transaction.getAmount());
            } else {
                result.put(wallet, transaction.getAmount());
            }
        }
        return result;
    }

    /** Thống kê thu theo loại ví. */
    public Map<Wallet, Double> calculateIncomeByWallet(List<Transaction> transactions) {
        ValidationService.validateTransactions(transactions);
        Map<Wallet, Double> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() != TransactionType.INCOME) {
                continue;
            }
            Wallet wallet = transaction.getWallet();
            if (result.containsKey(wallet)) {
                result.put(wallet,
                        result.get(wallet) + transaction.getAmount());
            } else {
                result.put(wallet, transaction.getAmount());
            }
        }
        return result;
    }

    /** Khung thống kê chi hoặc thu theo tháng. */
    public Map<YearMonth, Double> calculateMonthlyStatistics(List<Transaction> transactions,
                                                             TransactionType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        ValidationService.validateTransactions(transactions);
        Map<YearMonth, Double> result = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction == null) {
                continue;
            }
            if (transaction.getType() != type) {
                continue;
            }
            YearMonth month = YearMonth.from(transaction.getDate());
            if (result.containsKey(month)) {
                result.put(month,
                        result.get(month) + transaction.getAmount());
            } else {
                result.put(month, transaction.getAmount());
            }
        }
        return result;
    }

    /** Thống kê thu theo tháng. */
    public Map<YearMonth, Double> calculateIncomeByMonth(List<Transaction> transactions) {
        return calculateMonthlyStatistics(transactions, TransactionType.INCOME);
    }

    /** Thống kê chi theo tháng. */
    public Map<YearMonth, Double> calculateExpenseByMonth(List<Transaction> transactions) {
        return calculateMonthlyStatistics(transactions, TransactionType.EXPENSE);
    }

}
