package com.expensemanager.model.wallet;

import com.expensemanager.exception.InsufficientFundsException;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;

/** Ví tiền mặt: rút bao nhiêu trừ bấy nhiêu, không tính phí giao dịch. */
public class CashWallet extends Wallet {

    /** Khởi tạo ví tiền mặt. */
    public CashWallet(String id, String name, double balance, int userId) {
        super(id, name, balance, WalletType.CASH, userId);
    }

    /** Rút tiền từ ví tiền mặt, không cho rút vượt quá số dư hiện có. */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        if (amount > getBalance()) {
            throw new InsufficientFundsException(balance, amount);
        }
        setBalance(getBalance() - amount);
    }
}