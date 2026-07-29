package com.expensemanager.model.wallet;

import com.expensemanager.model.enums.WalletType;

/**
 * Ví tiền mặt: rút bao nhiêu trừ bấy nhiêu, không tính phí giao dịch.
 */
public class CashWallet extends Wallet {

    /**
     * Khởi tạo ví tiền mặt.
     *
     * @param id      định danh ví, phải >= 0
     * @param name    tên ví, không được rỗng
     * @param balance số dư ban đầu, phải >= 0
     * @throws IllegalArgumentException nếu id âm, name rỗng/null, hoặc balance âm
     */
    public CashWallet(int id, String name, double balance) {
        super(id, name, balance, WalletType.CASH);
    }

    /**
     * Rút tiền từ ví tiền mặt. Không cho rút vượt quá số dư hiện có.
     *
     * @param amount số tiền cần rút, phải > 0
     * @throws IllegalArgumentException nếu amount <= 0, hoặc amount vượt quá số dư hiện có
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0.");
        }
        if (amount > getBalance()) {
            throw new IllegalArgumentException("Ví tiền mặt không đủ số dư.");
        }
        setBalance(getBalance() - amount);
    }
}