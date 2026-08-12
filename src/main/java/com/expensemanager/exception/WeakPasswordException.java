package com.expensemanager.exception;

/** Thrown when a password does not meet the required strength criteria. */
public class WeakPasswordException extends ExpenseManagerException {

    private final String reason;

    /**
     * Khởi tạo đối tượng.
     *
     * @param reason lý do mật khẩu không đủ mạnh
     */
    public WeakPasswordException(String reason) {
        super("Mật khẩu không đủ mạnh: " + reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}