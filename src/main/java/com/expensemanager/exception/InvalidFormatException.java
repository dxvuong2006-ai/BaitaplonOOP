package com.expensemanager.exception;

import com.expensemanager.model.enums.FieldType;

/** Thrown when the format of a field is invalid. */
public class InvalidFormatException extends ExpenseManagerException {

    private final FieldType fieldType;
    private final String invalidValue;

    /**
     * Creates an exception for an invalid field format.
     *
     * @param fieldType trường dữ liệu có định dạng không hợp lệ
     * @param invalidValue giá trị không hợp lệ đã được truyền vào
     */
    public InvalidFormatException(FieldType fieldType, String invalidValue) {
        super(fieldType.getDisplayName() + " không đúng định dạng");
        this.fieldType = fieldType;
        this.invalidValue = invalidValue;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}