package com.expensemanager.service;

import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.exception.BudgetExceededException;

import java.util.Objects;
import java.time.LocalDate;


/** Quản lý ngân sách. */
public class BudgetService {


    /** Giúp lọc loại giao dịch chi tiêu rồi tính tổng tiền trong chu kỳ. */
    private double calculateTotalSpentAmount(Budget budget) {
        ExpenseManager manager = ExpenseManager.getInstance();
        LocalDate today = LocalDate.now();
        return manager.getTransactions().stream()
                .filter(transaction -> transaction instanceof Expense)
                .filter(transaction -> transaction.getCategory() != null)
                .filter(transaction -> Objects.equals(
                        transaction.getCategory(), budget.getCategory()))
                .filter(transaction -> DateUtils.isInSamePeriod(
                        transaction.getDate(), today, budget.getPeriod()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /** Kiểm tra ngân sách có bị vượt không. */
    public void validateBudgetLimit(Budget budget) {
        ValidationService.validateBudget(budget);
        double spent = calculateTotalSpentAmount(budget);
        if (budget.isExceeded(spent)) {
            throw new BudgetExceededException(budget.getLimitAmount(), spent);
        }
    }

    /** Lấy số tiền còn lại của ngân sách. */
    public double getRemainingBudget(Budget budget) {
        ValidationService.validateBudget(budget);
        double spent = calculateTotalSpentAmount(budget);
        return budget.getRemainingAmount(spent);
    }

    /** Tính phần trăm ngân sách đã sử dụng. */
    public double getUsagePercentage(Budget budget) {
        if (budget == null || budget.getLimitAmount() <= 0) {
            return 0;
        }
        double spent = calculateTotalSpentAmount(budget);
        return spent * 100 / budget.getLimitAmount();
    }
}
