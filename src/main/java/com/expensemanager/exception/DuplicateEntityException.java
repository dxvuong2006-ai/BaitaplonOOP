package com.expensemanager.exception;

/**
 * Thrown when attempting to create a duplicate entity.
 */
public class DuplicateEntityException extends ExpenseManagerException {

    private final String entityType;
    private final String entityName;

    public DuplicateEntityException(String entityType,
                                    String entityName) {

        super(entityType + " \"" + entityName + "\" already exists.");

        this.entityType = entityType;
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityName() {
        return entityName;
    }

}