package com.expensemanager.exception;

/** Lớp ngoại lệ gốc cho toàn bộ ứng dụng Expense Manager. */
public class ExpenseManagerException extends RuntimeException {

    /**
     * Khởi tạo đối tượng với thông điệp lỗi.
     *
     * @param message thông điệp mô tả lỗi
     */
    public ExpenseManagerException(String message) {
        super(message);
    }

    /**
     * Khởi tạo đối tượng với thông điệp lỗi và nguyên nhân gốc.
     *
     * @param message thông điệp mô tả lỗi
     * @param cause nguyên nhân gốc gây ra lỗi
     */
    public ExpenseManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}