package com.expensemanager.model.enums;

/** Enum định nghĩa loại giao dịch tài chính. */
public enum TransactionType {
    INCOME("Income"),
    EXPENSE("Expense"),
    RECURRING_EXPENSE("Recurring Expense");

    private final String displayName;

    /** Khởi tạo nhãn hiển thị cho từng loại giao dịch. */
    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    /** Lấy nhãn hiển thị của loại giao dịch. */
    public String getDisplayName() {
        return displayName;
    }

    /** Trả về nhãn hiển thị của loại giao dịch, dùng khi in trực tiếp đối tượng. */
    @Override
    public String toString() {
        return displayName;
    }
}