package com.expensemanager.exception;

/** Thrown when the provided username or password is incorrect. */
public class InvalidCredentialException extends ExpenseManagerException {

    /**
     * Khởi tạo đối tượng với thông điệp lỗi tùy chỉnh.
     *
     * @param message thông điệp mô tả lỗi
     */
    public InvalidCredentialException(String message) {
        super(message);
    }

    /** Khởi tạo đối tượng với thông điệp lỗi mặc định. */
    public InvalidCredentialException() {
        super("Tên đăng nhập hoặc mật khẩu không chính xác");
    }
}