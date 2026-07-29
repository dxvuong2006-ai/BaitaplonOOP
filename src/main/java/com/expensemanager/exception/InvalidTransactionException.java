package com.expensemanager.exception;

/**
 * Thrown when transaction data is invalid.
 */
public class InvalidTransactionException extends ExpenseManagerException {

    private final String reason;

    public InvalidTransactionException(String reason) {

        super(reason);

        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}