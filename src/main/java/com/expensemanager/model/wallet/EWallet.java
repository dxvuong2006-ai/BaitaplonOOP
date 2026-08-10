package com.expensemanager.model.wallet;

import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.utils.CurrencyUtils;

/** Ví điện tử: rút tiền tính thêm phí giao dịch theo tỉ lệ phần trăm. */
public class EWallet extends Wallet {
    private double feePercent;

    /** Khởi tạo ví điện tử mặc định không tính phí (feePercent = 0). */
    public EWallet(String id, String name, double balance, int userId) {
        this(id, name, balance, 0.0, userId);
    }

    /** Khởi tạo ví điện tử với tỉ lệ phí giao dịch xác định. */
    public EWallet(String id, String name, double balance, double feePercent, int userId) {
        super(id, name, balance, WalletType.EWALLET, userId);
        setFeePercent(feePercent);
    }

    /** Lấy tỉ lệ phí giao dịch (tính theo %). */
    public double getFeePercent() {
        return feePercent;
    }

    /** Gán lại tỉ lệ phí giao dịch. */
    public void setFeePercent(double feePercent) {
        if (feePercent < 0) {
            throw new NegativeValueException(FieldType.FEEPERCENT);
        }
        this.feePercent = feePercent;
    }

    /** Rút tiền từ ví điện tử, số tiền bị trừ gồm cả phí tính theo tỉ lệ phần trăm. */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        double fee = amount * (feePercent / 100.0);
        double totalDeduction = amount + fee;
        if (totalDeduction > getBalance() + CurrencyUtils.EPSILON) {
            throw new IllegalArgumentException(
                    "Ví điện tử không đủ số dư để thực hiện rút tiền và thanh toán phí.");
        }
        setBalance(getBalance() - totalDeduction);
    }
}