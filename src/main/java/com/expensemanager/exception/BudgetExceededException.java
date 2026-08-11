package com.expensemanager.exception;

/** Thrown when a budget limit is exceeded. */
public class BudgetExceededException extends ExpenseManagerException {

    private final double budgetLimit;
    private final double currentSpent;

    /**
     * Khởi tạo đối tượng.
     *
     * @param budgetLimit hạn mức ngân sách
     * @param currentSpent số tiền đã chi tiêu
     */
    public BudgetExceededException(double budgetLimit, double currentSpent) {
        super(String.format("Đã vượt quá ngân sách %.2f", currentSpent - budgetLimit));
        this.budgetLimit = budgetLimit;
        this.currentSpent = currentSpent;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public double getCurrentSpent() {
        return currentSpent;
    }

    public double getExceededAmount() {
        return currentSpent - budgetLimit;
    }
}