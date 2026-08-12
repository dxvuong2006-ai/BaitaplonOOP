package com.expensemanager.exception;

import com.expensemanager.model.enums.FieldType;

/** Thrown when a required field is empty. */
public class EmptyFieldException extends ExpenseManagerException {

    private final FieldType fieldType;

    /**
     * Khởi tạo đối tượng.
     *
     * @param fieldType trường dữ liệu bị bỏ trống
     */
    public EmptyFieldException(FieldType fieldType) {
        super(fieldType.getDisplayName() + " không được để trống.");
        this.fieldType = fieldType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}