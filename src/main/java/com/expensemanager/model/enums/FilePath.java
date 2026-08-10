package com.expensemanager.model.enums;

/** Enum xác định tên file dữ liệu tương ứng cho từng loại đối tượng. */
public enum FilePath {
    WALLET("wallets"),
    TRANSACTION("transactions"),
    CATEGORY("categories"),
    BUDGET("budgets"),
    USER("users");

    private final String basePath;
    private final String resource;

    /** Khởi tạo FilePath với tên file gốc, thư mục resource được gán mặc định. */
    FilePath(String basePath) {
        this.basePath = basePath;
        this.resource = "src/main/resources/data/";
    }

    /** Sinh đường dẫn hoàn chỉnh dựa vào StorageType (VD: CSV -> "data/csv/wallets.csv"). */
    public String getFullPath(StorageType storageType) {
        // Lấy tên enum (CSV hoặc JSON) chuyển thành chữ thường làm đuôi file
        String extension = storageType.name().toLowerCase();
        return this.resource + extension + "/" + this.basePath + "." + extension;
    }
}