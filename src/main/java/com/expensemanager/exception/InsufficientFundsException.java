package com.expensemanager.exception;

/** Thrown when a wallet does not have enough balance. */
public class InsufficientFundsException extends ExpenseManagerException {

    private final double currentBalance;
    private final double requiredAmount;

    /**
     * Khởi tạo đối tượng.
     *
     * @param currentBalance số dư hiện tại của ví
     * @param requiredAmount số tiền cần thiết để thực hiện giao dịch
     */
    public InsufficientFundsException(double currentBalance, double requiredAmount) {
        super("Số dư của ví không đủ để thực hiện giao dịch.");
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getRequiredAmount() {
        return requiredAmount;
    }

    public double getShortage() {
        return requiredAmount - currentBalance;
    }
}