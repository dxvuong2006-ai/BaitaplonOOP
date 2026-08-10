package com.expensemanager.exception;

public class InvalidCredentialException extends ExpenseManagerException {
    public InvalidCredentialException(String message) {
        super(message);
    }

    public InvalidCredentialException() {
        super("Tên đăng nhập hoặc mật khẩu không chính xác");
    }
}
