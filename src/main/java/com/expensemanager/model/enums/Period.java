package com.expensemanager.model.enums;

/** Enum xác định chu kỳ lặp lại của giao dịch định kỳ hoặc kỳ hạn ngân sách. */
public enum Period {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTH("Month"),
    YEARLY("Yearly");

    private final String displayName;

    Period(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
