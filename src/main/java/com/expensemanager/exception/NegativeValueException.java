package com.expensemanager.exception;

import com.expensemanager.model.enums.FieldType;

/**
 * Thrown when a numeric field has a negative value.
 */
public class NegativeValueException extends ExpenseManagerException {

    private final FieldType fieldType;

    public NegativeValueException(FieldType fieldType) {
        super(fieldType + " không thể âm");
        this.fieldType = fieldType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}