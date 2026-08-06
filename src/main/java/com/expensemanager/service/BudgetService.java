package com.expensemanager.service;

import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.utils.DateUtils;

import com.expensemanager.exception.BudgetExceededException;
import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;


/** Quản lý ngân sách. */
public class BudgetService {

    private final List<Budget> budgets;

    public BudgetService() {
        budgets = new ArrayList<>();
    }

    /** Thêm ngân sách. */
    public void addBudget(Budget budget) {
        ValidationService.validateBudget(budget);
        budgets.add(budget);
    }

    /** Xóa ngân sách. */
    public void removeBudget(Budget budget) {
        ValidationService.validateBudget(budget);
        budgets.remove(budget);
    }

    /** Cập nhật ngân sách. */
    public void updateBudget(Budget oldBudget, Budget newBudget) {
        ValidationService.validateBudget(oldBudget);
        ValidationService.validateBudget(newBudget);
        Budget existed = findBudgetByCategory(newBudget.getCategory());
        if (existed != null && existed != oldBudget) {
            throw new DuplicateEntityException("Ngân sách", newBudget.getCategory().getName());
        }
        oldBudget.setCategory(newBudget.getCategory());
        oldBudget.setLimitAmount(newBudget.getLimitAmount());
        oldBudget.setPeriod(newBudget.getPeriod());
    }

    /** Tìm theo ID. */
    public Budget findBudgetById(String id) {
        for (Budget budget : budgets) {
            if (budget.getId() == id) {
                return budget;
            }
        }
        return null;
    }

    /** Tìm kiếm ngân sách bằng danh mục. */
    public Budget findBudgetByCategory(Category category) {
        ValidationService.validateCategory(category);
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

    /** Giúp lọc loại giao dịch thu tiêu rồi tính tổng tiền trong chu kỳ. */
    private double calculateTotalIncomeAmount(Budget budget) {
        ExpenseManager manager = ExpenseManager.getInstance();
        LocalDate today = LocalDate.now();
        return manager.getTransactions().stream()
                .filter(transaction -> transaction instanceof Income)
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
