package com.expensemanager.validation;

import com.expensemanager.exception.ExpenseManagerException;
import com.expensemanager.service.ValidationService;

/** Nhận kết quả kiểm tra từ InputValidationService. */
public final class ValidationResult<T> {
    private final boolean valid;
    private final ExpenseManagerException exception;
    private final T value;

    /** Khởi tạo. */
    public ValidationResult(boolean valid, ExpenseManagerException exception, T value) {
        this.valid = valid;
        this.exception = exception;
        this.value = value;
    }

    /** . */
    public static <T> ValidationResult<T> success(T value) {
        return new ValidationResult<>(true, null, value);
    }

    /** . */
    public static <T> ValidationResult<T> failure(ExpenseManagerException exception) {
        return new ValidationResult<>(false, exception, null);
    }

    /** . */
    public boolean isValid() {
        return valid;
    }

    /** . */
    public boolean hasError() {
        return !valid;
    }

    /** . */
    public ExpenseManagerException getException() {
        return exception;
    }

    /** . */
    public String getMessage() {
        return exception == null ? null : exception.getMessage();
    }

    /** . */
    public T getValue() {
        return value;
    }

}
