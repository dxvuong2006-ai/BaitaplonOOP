package com.expensemanager.model.enums;

/** Enum định nghĩa loại giao dịch tài chính. */
public enum TransactionType {
    INCOME("Income"),
    EXPENSE("Expense"),
    RECURRING_EXPENSE("Recurring Expense");

    private final String displayName;

    /** Constructor khởi tạo nhãn hiển thị cho từng loại giao dịch. */
    TransactionType(String displayName) {
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
