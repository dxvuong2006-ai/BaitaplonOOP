package com.expensemanager.model.enums;

/** Enum phân loại các hình thức lưu trữ tiền tệ (Ví). */
public enum WalletType {
    CASH("Cash"),
    BANK("Bank"),
    EWALLET("Ewallet");

    private final String displayName;

    /** Constructor. */
    WalletType(String displayName) {
        this.displayName = displayName ;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Phương thức ghi đè. */
    @Override
    public String toString() {
        return displayName;
    }
}
