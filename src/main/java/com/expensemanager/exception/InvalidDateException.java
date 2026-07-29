package com.expensemanager.exception;

/**
 * Thrown when a date is invalid.
 */
public class InvalidDateException extends ExpenseManagerException {

    private final String invalidDate;

    public InvalidDateException(String invalidDate) {

        super("Định dạng ngày tháng không hợp lệ: " + invalidDate);

        this.invalidDate = invalidDate;
    }

    public String getInvalidDate() {
        return invalidDate;
    }

}