package com.expensemanager.exception;

/**
 * Thrown when a wallet does not have enough balance.
 */
public class InsufficientFundsException extends ExpenseManagerException {

    private final double currentBalance;
    private final double requiredAmount;

    public InsufficientFundsException(double currentBalance,
                                      double requiredAmount) {

        super("Số dư hiện tại không đủ để hoàn tất giao dịch.");

        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }

    public double getShortage() {
        return requiredAmount - currentBalance;
    }

}