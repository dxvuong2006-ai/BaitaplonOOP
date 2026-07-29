package com.expensemanager.exception;

/** Xu li loi amoun nho hon zero. */
public class NegativeAmountException extends ExpenseManagerException{
    public NegativeAmountException() {
        super("Giá trị amount phải lớn hơn không");
    }

    public NegativeAmountException(String message) {
        super(message);
    }
}
