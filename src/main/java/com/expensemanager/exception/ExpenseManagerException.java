package com.expensemanager.exception;

public class ExpenseManagerException extends RuntimeException {
    public ExpenseManagerException(String message) {
        super(message);
    }

    public ExpenseManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
