package com.expensemanager.factory.model;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.wallet.Wallet;

/** Lớp khởi tạo đối tượng Wallet theo mẫu Factory Method. */
public class WalletFactory {

    private WalletFactory() {
        // Không cho phép tạo đối tượng Factory từ bên ngoài
    }

    public static Wallet createWallet(String id,
                                      String name,
                                      double balance,
                                      WalletType type,
                                      double extraFee,
                                      int userId) {
        // 1. Chặn lỗi NullPointerException trước khi vào switch-case
        if (type == null) {
            throw new EmptyFieldException(FieldType.WALLETTYPE);
        }
        // 2. Phân luồng khởi tạo đối tượng
        switch (type) {
            case CASH:
                return new CashWallet(id, name, balance, userId);
            case BANK:
                // Truyền extraFee vào làm transactionFee
                return new BankAccount(id, name, balance, extraFee, userId);
            case EWALLET:
                // Truyền extraFee vào làm feePercent
                return new EWallet(id, name, balance, extraFee, userId);
            default:
                // 3. Khởi tạo ngoại lệ đúng chuẩn (FieldType + Giá trị bị sai)
                throw new InvalidFormatException(FieldType.WALLETTYPE, String.valueOf(type));
        }
    }
}
