package com.expensemanager.model.enums;

public enum FieldType {
    AMOUNT("Số tiền"),
    CATEGORY("Danh mục"),
    WALLET("Ví"),
    NOTE("Ghi chú"),
    DATE("Ngày"),
    ID("Mã"),
    NAME("Tên"),
    BALANCE("Số dư"),
    WALLETTYPE("Loại ví"),
    SOURCE("Nguồn"),
    PAYMENTMETHOD("Phương thức thanh toán"),
    PERIOD("Chu kỳ"),
    AMOUNTSTR("Số tiền"),
    TRANSACTIONFEE("Phí giao dịch"),
    FEEPERCENT("Phần trăm phí"),
    LIMITAMOUNT("Giới hạn"),
    TRANSACTIONTYPE("Loại giao dịch"),
    STORAGETYPE("Kiểu lưu trữ"),
    REGISTRY("Sổ đăng ký"),
    TRANSACTION("Giao dịch"),
    BUDGET("Ngân sách"),
    REPORTTYPE("Loại báo cáo"),
    USERID("Người dùng");

    private final String displayName;

    /** Đặt loại FieldType. */
    FieldType(String displayName) {
        this.displayName = displayName;
    }

    /** Lấy tên FieldType. */
    public String getDisplayName() {
        return displayName;
    }
}
