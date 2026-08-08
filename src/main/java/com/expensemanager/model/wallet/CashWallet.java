package com.expensemanager.model.wallet;

import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.exception.InsufficientFundsException;

/**
 * Ví tiền mặt: rút bao nhiêu trừ bấy nhiêu, không tính phí giao dịch.
 */
public class CashWallet extends Wallet {


    public CashWallet(String id, String name, double balance) {
        super(id, name, balance, WalletType.CASH);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > getBalance()) {
            throw new InsufficientFundsException(balance,amount);
        }
        setBalance(getBalance() - amount);
    }
}