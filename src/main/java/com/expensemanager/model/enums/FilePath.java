package com.expensemanager.model.enums;

public enum FilePath {
    WALLET("data/wallets"),
    TRANSACTION("data/transactions"),
    CATEGORY("data/categories"),
    BUDGET("data/budgets");

    private final String basePath;

    // Constructor của Enum
    FilePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Hàm tự động sinh đường dẫn hoàn chỉnh dựa vào StorageType
     * VD: Nhét StorageType.CSV vào -> trả về "data/wallets.csv"
     */
    public String getFullPath(StorageType storageType) {
        // Lấy tên enum (CSV hoặc JSON) chuyển thành chữ thường làm đuôi file
        String extension = storageType.name().toLowerCase();
        return this.basePath + "." + extension;
    }
}