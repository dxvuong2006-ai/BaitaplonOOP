package com.expensemanager.model.wallet;

import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.exception.InsufficientFundsException;

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
     * @param userId  id của người dùng sở hữu ví, phải > 0
     * @throws IllegalArgumentException nếu id âm, name rỗng/null, balance âm, hoặc userId không hợp lệ
     */
    public CashWallet(String id, String name, double balance, int userId) {   // sửa: +userId
        super(id, name, balance, WalletType.CASH, userId);                     // sửa: +userId
    }

    @Override
    public void withdraw(double amount) {
        if (amount > getBalance()) {
            throw new InsufficientFundsException(balance,amount);
        }
        setBalance(getBalance() - amount);
    }
}