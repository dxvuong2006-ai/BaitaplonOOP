package com.expensemanager.model.enums;

public enum StorageType {
    CSV("CSV"),
    JSON("JSON");

    private final String displayName;

    StorageType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
