package com.expensemanager.service;

import com.expensemanager.factory.storage.BudgetStorageFactory;
import com.expensemanager.factory.storage.CategoryStorageFactory;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.budget.BudgetRecord;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.FilePath;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.repository.Storage;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.enums.Period;

import com.expensemanager.exception.BudgetExceededException;
import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;

import javax.swing.plaf.SeparatorUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;


/** Quản lý ngân sách. */
public class BudgetService {

    private final List<Budget> budgets = new ArrayList<>();
    private final BudgetStorageFactory storageFactory;
    private final CategoryService categoryService;

    public BudgetService(BudgetStorageFactory storageFactory, CategoryService categoryService) {
        this.storageFactory = storageFactory;
        this.categoryService = categoryService;
        load();
    }

    public void load() {
        budgets.clear();
        List<BudgetRecord> records = storageFactory.load(FilePath.BUDGET);
        for (BudgetRecord record : records) {
            try {
                budgets.add(toBudget(record));
            } catch(Exception e) {
                System.err.println("Bỏ qua ngân sách id = " + record.getId()
                                    + " do lỗi " + e.getMessage());
            }
        }
    }

    public void save() {
        List<BudgetRecord> records = new ArrayList<>();
        for(Budget budget : budgets) {
            records.add(toRecord(budget));
        }
        storageFactory.save(FilePath.BUDGET, records);
    }

    /** Dựng lại Budget thật từ BudgetRecord (categoryId -> Category thật). */
    private Budget toBudget(BudgetRecord record) {
        Category category = categoryService.findCategoryById(record.getCategoryId(), record.getUserId());
        ValidationService.validateCategory(category);
        Period period = Period.valueOf(record.getPeriod());
        return new Budget(record.getId(), category, record.getLimitAmount(), period, record.getUserId());
    }

    /** Chuyển Budget thật thành BudgetRecord (Category thật -> categoryId dạng String). */
    private BudgetRecord toRecord(Budget budget) {
        return new BudgetRecord(
                budget.getId(),
                budget.getCategory() != null ? budget.getCategory().getId() : "",
                budget.getLimitAmount(),
                budget.getPeriod().name(),
                budget.getUserId()
        );
    }

    /** Thêm ngân sách. */
    public void addBudget(Budget budget, int userId) {
        ValidationService.validateBudget(budget);
        if (findBudgetByCategory(budget.getCategory(), userId) != null) {
            throw new DuplicateEntityException("Ngân sách", "mã" + userId);
        }
        budget.setUserId(userId);
        budgets.add(budget);
        save();
    }

    /** Xóa ngân sách. */
    public void removeBudget(Budget budget, int userId) {
        ValidationService.validateBudget(budget);
        Budget existing = findBudgetById(budget.getId(), userId);
        budgets.remove(existing);
        save();
    }

    /** Cập nhật ngân sách. */
    public void updateBudget(Budget oldBudget, Budget newBudget, int userId) {
        ValidationService.validateBudget(oldBudget);
        ValidationService.validateBudget(newBudget);
        Budget existingOld = findBudgetById(oldBudget.getId(), userId);
        ValidationService.validateBudget(existingOld);
        Budget existed = findBudgetByCategory(newBudget.getCategory(), userId);
        if (existed != null && existed != oldBudget) {
            throw new DuplicateEntityException("Ngân sách", newBudget.getCategory().getName());
        }
        oldBudget.setCategory(newBudget.getCategory());
        oldBudget.setLimitAmount(newBudget.getLimitAmount());
        oldBudget.setPeriod(newBudget.getPeriod());
        oldBudget.setUserId(newBudget.getUserId());
        save();
    }

    /** Tìm theo ID. */
    public Budget findBudgetById(String id, int userId) {
        for (Budget budget : getBudgets(userId)) {
            if (budget.getId().equals(id) && budget.getUserId() == userId) {
                return budget;
            }
        }
        return null;
    }

    /** Tìm kiếm ngân sách bằng danh mục. */
    public Budget findBudgetByCategory(Category category, int userId) {
        ValidationService.validateCategory(category);
        for (Budget budget : getBudgets(userId)) {
            if (Objects.equals(budget.getCategory(), category) && budget.getUserId() == userId) {
                return budget;
            }
        }
        return null;
    }

    /** Danh sách các loại ngân sách. */
    public List<Budget> getBudgets(int userId) {
        return budgets.stream()
                .filter(budget -> budget.getUserId() == userId)
                .toList();
    }

    /** Giúp lọc loại giao dịch chi tiêu rồi tính tổng tiền trong chu kỳ. */
    private double calculateTotalSpentAmount(Budget budget, int userId) {
        ExpenseManager manager = ExpenseManager.getInstance();
        LocalDate today = LocalDate.now();
        return manager.getTransactions().stream()
                .filter(transaction -> transaction.getUserId() == userId)
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
    private double calculateTotalIncomeAmount(Budget budget, int userId) {
        ExpenseManager manager = ExpenseManager.getInstance();
        LocalDate today = LocalDate.now();
        return manager.getTransactions().stream()
                .filter(transaction -> transaction.getUserId() == userId)
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
    public void validateBudgetLimit(Budget budget, double pendingAmount, int userId) {
        ValidationService.validateBudget(budget);
        double spent = calculateTotalSpentAmount(budget, userId) + pendingAmount;
        if (budget.isExceeded(spent)) {
            throw new BudgetExceededException(budget.getLimitAmount(), spent);
        }
    }

    /** Lấy số tiền còn lại của ngân sách. */
    public double getRemainingBudget(Budget budget, int userId) {
        ValidationService.validateBudget(budget);
        double spent = calculateTotalSpentAmount(budget, userId);
        return budget.getRemainingAmount(spent);
    }

    /** Tính phần trăm ngân sách đã sử dụng. */
    public double getUsagePercentage(Budget budget, int userId) {
        if (budget == null || budget.getLimitAmount() <= 0) {
            return 0;
        }
        double spent = calculateTotalSpentAmount(budget, userId);
        return spent * 100 / budget.getLimitAmount();
    }
}
