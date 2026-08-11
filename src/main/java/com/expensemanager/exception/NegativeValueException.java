package com.expensemanager.exception;

import com.expensemanager.model.enums.FieldType;

/** Thrown when a numeric field has a negative value. */
public class NegativeValueException extends ExpenseManagerException {

    private final FieldType fieldType;

    /**
     * Khởi tạo đối tượng.
     *
     * @param fieldType trường dữ liệu có giá trị âm
     */
    public NegativeValueException(FieldType fieldType) {
        super(fieldType.getDisplayName() + " không thể âm");
        this.fieldType = fieldType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}