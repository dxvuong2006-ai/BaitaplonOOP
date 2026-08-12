package com.expensemanager.factory.model;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.wallet.Wallet;

/** Lớp khởi tạo đối tượng {@link Wallet} theo mẫu Factory Method. */
public final class WalletFactory {

    /** Không cho phép tạo đối tượng Factory từ bên ngoài. */
    private WalletFactory() {}

    /**
     * Tạo mới một đối tượng {@link Wallet} dựa trên {@code type} được truyền vào.
     *
     * @param id mã định danh ví
     * @param name tên ví
     * @param balance số dư ban đầu của ví
     * @param type loại ví (CASH, BANK, EWALLET)
     * @param extraFee phí giao dịch hoặc phí phần trăm áp dụng (tùy loại ví)
     * @param userId mã người dùng sở hữu ví
     * @return đối tượng {@link Wallet} tương ứng với loại được chỉ định
     * @throws EmptyFieldException nếu {@code type} là null
     */
    public static Wallet createWallet(
            String id, String name, double balance, WalletType type, double extraFee, int userId) {
        // 1. Chặn lỗi NullPointerException trước khi vào switch-case
        if (type == null) {
            throw new EmptyFieldException(FieldType.WALLETTYPE);
        }

        // 2. Phân luồng khởi tạo đối tượng
        return switch (type) {
            case CASH -> new CashWallet(id, name, balance, userId);

            // Truyền extraFee vào làm transactionFee
            case BANK -> new BankAccount(id, name, balance, extraFee, userId);

            // Truyền extraFee vào làm feePercent
            case EWALLET -> new EWallet(id, name, balance, extraFee, userId);
        };
    }
}