package com.expensemanager.exception;

/** Xu li loi amoun nho hon zero. */
public class NegativeBalanceException extends ExpenseManagerException{
    public NegativeBalanceException() {
        super("Giá trị Balance phải lớn hơn không");
    }

    public NegativeBalanceException(String message) {
        super(message);
    }
}
