package com.expensemanager.model.enums;

/** Enum xác định định dạng file dùng để lưu trữ dữ liệu. */
public enum StorageType {
    CSV("CSV"),
    JSON("JSON");

    private final String displayName;

    /** Khởi tạo kiểu lưu trữ với tên hiển thị tương ứng. */
    StorageType(String displayName) {
        this.displayName = displayName;
    }

    /** Lấy tên hiển thị của kiểu lưu trữ. */
    public String getDisplayName() {
        return displayName;
    }
}