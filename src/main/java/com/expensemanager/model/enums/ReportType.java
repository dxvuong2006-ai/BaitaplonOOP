package com.expensemanager.model.enums;

/** Enum xác định định dạng file khi xuất báo cáo. */
public enum ReportType {
    EXCEL("EXCEL"),
    PDF("PDF");

    private final String displayName;

    /** Khởi tạo loại báo cáo với tên hiển thị tương ứng. */
    ReportType(String displayName) {
        this.displayName = displayName;
    }

    /** Lấy tên hiển thị của loại báo cáo. */
    public String getDisplayName() {
        return displayName;
    }
}