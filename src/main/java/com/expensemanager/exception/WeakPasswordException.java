package com.expensemanager.exception;

public class WeakPasswordException extends ExpenseManagerException{
    private final String reason;

    public WeakPasswordException(String reason) {
        super("Mật khẩu không đủ mạnh: " + reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
