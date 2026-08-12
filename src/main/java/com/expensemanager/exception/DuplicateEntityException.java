package com.expensemanager.exception;

/** Thrown when attempting to create a duplicate entity. */
public class DuplicateEntityException extends ExpenseManagerException {

    private final String entityType;
    private final String entityName;

    /**
     * Khởi tạo đối tượng.
     *
     * @param entityType loại thực thể bị trùng lặp
     * @param entityName tên thực thể bị trùng lặp
     */
    public DuplicateEntityException(String entityType, String entityName) {
        super(entityType + " \"" + entityName + "\" đã tồn tại.");
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