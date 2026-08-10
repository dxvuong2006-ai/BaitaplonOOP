package com.expensemanager.model.enums;

/** Enum phân loại các hình thức lưu trữ tiền tệ (Ví). */
public enum WalletType {
    CASH("Cash"),
    BANK("Bank"),
    EWALLET("Ewallet");

    private final String displayName;

    /** Khởi tạo loại ví với tên hiển thị tương ứng. */
    WalletType(String displayName) {
        this.displayName = displayName;
    }

    /** Lấy tên hiển thị của loại ví. */
    public String getDisplayName() {
        return displayName;
    }

    /** Trả về tên hiển thị của loại ví, dùng khi in trực tiếp đối tượng. */
    @Override
    public String toString() {
        return displayName;
    }
}