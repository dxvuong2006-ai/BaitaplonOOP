package com.expensemanager.exception;

/**
 * Thrown when a wallet does not have enough balance.
 */
public class InsufficientFundsException extends ExpenseManagerException {

    private final double currentBalance;
    private final double requiredAmount;

    public InsufficientFundsException(double currentBalance,
                                      double requiredAmount) {

        super("Tài khoản ngân hàng không đủ số dư để thực hiện rút tiền và thanh toán phí giao dịch.");

        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }

    public double getShortage() {
        return requiredAmount - currentBalance;
    }

}