package com.expensemanager.model.enums;

public enum RecurringExecutionStatus {

    SUCCESS("Thành công"),
    FAILED("Thất bại");

    /** Tên hiển thị của trạng thái. */
    private final String displayName;

    /** Khởi tạo trạng thái thực thi. */
    RecurringExecutionStatus(String displayName) {
        this.displayName = displayName;
    }

    /** Trả về tên hiển thị của trạng thái. */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
