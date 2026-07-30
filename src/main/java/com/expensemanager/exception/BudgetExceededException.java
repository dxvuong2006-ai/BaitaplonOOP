package com.expensemanager.exception;

/**
 * Thrown when a budget limit is exceeded.
 */
public class BudgetExceededException extends ExpenseManagerException {

    private final double budgetLimit;
    private final double currentSpent;

    public BudgetExceededException(double budgetLimit,
                                   double currentSpent) {

        super("Vượt quá ngân sách cho phép");

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