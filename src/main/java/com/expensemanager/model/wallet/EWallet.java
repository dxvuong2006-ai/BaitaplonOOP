package com.expensemanager.model.wallet;

import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.utils.CurrencyUtils;

public class EWallet extends Wallet {
    private double feePercent;

    public EWallet(String id, String name, double balance, int userId) {
        this(id, name, balance, 0.0, userId);
    }

    public EWallet(String id, String name, double balance, double feePercent, int userId) {
        super(id, name, balance, WalletType.EWALLET, userId);
        setFeePercent(feePercent);
    }

    public double getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(double feePercent) {
        if (feePercent < 0) {
            throw new NegativeValueException(FieldType.FEEPERCENT);
        }
        this.feePercent = feePercent;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        double fee = amount * (feePercent / 100.0);
        double totalDeduction = amount + fee;
        if (totalDeduction > getBalance() + CurrencyUtils.EPSILON) {
            throw new IllegalArgumentException("Ví điện tử không đủ số dư để thực hiện rút tiền và thanh toán phí.");
        }
        setBalance(getBalance() - totalDeduction);
    }
}