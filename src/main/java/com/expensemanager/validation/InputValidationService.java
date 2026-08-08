package com.expensemanager.validation;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.service.ValidationService;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.validation.ValidationResult;
import com.expensemanager.model.enums.FieldType;

import java.time.LocalDate;
import java.util.Objects;

/** Lớp kiểm tra dữ liệu đầu vào. */
public final class InputValidationService {

    /** Ngăn không cho khởi tạo đối tượng bên ngoài. */
    private InputValidationService() {
        throw new UnsupportedOperationException("Không thể khởi tạo đối tượng");
    }

    /** Kiểm tra bắt buộc không được để rỗng. */
    public static ValidationResult<String> validateRequired(String value, FieldType fieldType) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.failure(new EmptyFieldException(fieldType));
        }
        return ValidationResult.success(value.trim());
    }

    /** Kiểm tra đối tượng được chọn. */
    public static <T> ValidationResult<T> validateSelection(T value, FieldType fieldType) {
        if (Objects.isNull(value)) {
            return ValidationResult.failure(new EmptyFieldException(fieldType));
        }
        return ValidationResult.success(value);
    }

    /** Kiểm tra độ dài chuỗi. */
    public static ValidationResult<String> validateLength(String value, int max, FieldType fieldType) {
        ValidationResult<String> result = validateRequired(value, fieldType);
        if (result.hasError()) {
            return result;
        }
        value = result.getValue();
        if (value.length() > max) {
            return ValidationResult.failure(new InvalidFormatException(fieldType, "quá " + max + " kí tự"));
        }
        return ValidationResult.success(value);
    }

    /** Kiểm tra tên hợp lệ. */
    public static ValidationResult<String> validateName(String value, FieldType fieldType, int max) {
        ValidationResult<String> result = validateLength(value, max, fieldType);
        if (result.hasError()) {
            return result;
        }
        return ValidationResult.success(result.getValue());
    }

    /** Kiểm tra số tiền. */
    public static ValidationResult<Double> validateAmount(String value) {
        ValidationResult<String> result = validateRequired(value, FieldType.AMOUNT);
        if (result.hasError()) {
            return ValidationResult.failure(result.getException());
        }
        try {
            double amount = CurrencyUtils.parseAmount(result.getValue());
            if (amount <= 0) {
                return ValidationResult.failure(new NegativeValueException(FieldType.AMOUNT));
            }
            return ValidationResult.success(amount);
        } catch (Exception e) {
            return ValidationResult.failure(new InvalidFormatException(FieldType.AMOUNT, "không hợp lệ"));
        }
    }

    /** Kiểm tra ngày hợp lệ. */
    public static ValidationResult<LocalDate> validateDate(String value) {
        ValidationResult<String> result = validateRequired(value, FieldType.DATE);
        if (result.hasError()) {
            return ValidationResult.failure(result.getException());
        }
        try {
            LocalDate date = DateUtils.parseDate(result.getValue());
            return ValidationResult.success(date);
        } catch (Exception e) {
            return ValidationResult.failure(new InvalidFormatException(FieldType.DATE, "không hợp lệ"));
        }
    }
}
