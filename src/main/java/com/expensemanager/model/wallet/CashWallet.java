package com.expensemanager.model.wallet;

/**
 * Ví tiền mặt: rút bao nhiêu trừ bấy nhiêu, không tính phí giao dịch.
 */
public class CashWallet extends wallet {

    /**
     * Khởi tạo ví tiền mặt.
     *
     * @param id định danh ví
     * @param name tên ví
     * @param balance số dư ban đầu
     */
    public CashWallet(int id, String name, double balance) {
        super(id, name, balance, WalletType.CASH);
    }

    /**
     * Rút tiền từ ví tiền mặt. Không cho rút vượt quá số dư hiện có.
     *
     * @param amount số tiền cần rút, phải > 0
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