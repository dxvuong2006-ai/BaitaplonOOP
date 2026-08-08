package com.expensemanager.exception;

import com.expensemanager.model.enums.FieldType;

/**
 * Thrown when a required field is empty.
 */
public class EmptyFieldException extends ExpenseManagerException {

    private final FieldType fieldType;

    public EmptyFieldException(FieldType fieldType) {
        super(fieldType.getDisplayName() + " không được để trống.");
        this.fieldType = fieldType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}
