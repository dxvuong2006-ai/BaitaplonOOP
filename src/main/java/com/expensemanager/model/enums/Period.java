package com.expensemanager.model.enums;

/** Enum xác định chu kỳ lặp lại của giao dịch định kỳ hoặc kỳ hạn ngân sách. */
public enum Period {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTH("Month"),
    YEARLY("Yearly");

    private final String displayName;

    /** Khởi tạo chu kỳ với tên hiển thị tương ứng. */
    Period(String displayName) {
        this.displayName = displayName;
    }

    /** Lấy tên hiển thị của chu kỳ. */
    public String getDisplayName() {
        return displayName;
    }

    /** Trả về tên hiển thị của chu kỳ, dùng khi in trực tiếp đối tượng. */
    @Override
    public String toString() {
        return displayName;
    }
}