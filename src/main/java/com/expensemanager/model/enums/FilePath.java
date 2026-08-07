package com.expensemanager.model.enums;

public enum FilePath {
    WALLET("wallets"),
    TRANSACTION("transactions"),
    CATEGORY("categories"),
    BUDGET("budgets");

    private final String basePath;
    private final String resource ;
    // Constructor của Enum
    FilePath(String basePath) {
        this.basePath = basePath;
        this.resource = "src/main/resources/data/";
    }

    /**
     * Hàm tự động sinh đường dẫn hoàn chỉnh dựa vào StorageType
     * VD: Nhét StorageType.CSV vào -> trả về "data/wallets.csv"
     */
    public String getFullPath(StorageType storageType) {
        // Lấy tên enum (CSV hoặc JSON) chuyển thành chữ thường làm đuôi file
        String extension = storageType.name().toLowerCase();
        return this.resource + extension + "/"+ this.basePath + "." + extension;
    }
}
