package com.expensemanager.model.wallet;

import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.exception.*;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.utils.CurrencyUtils;

/**
 * Lớp đại diện cho Ví / Tài khoản ngân hàng (BankAccount).
 * Kế thừa từ lớp trừu tượng Wallet.
 * Thể hiện tính đa hình qua phương thức withdraw(), bao gồm logic tính phí giao dịch cố định.
 */
public class BankAccount extends Wallet {
    private double transactionFee; // Phí giao dịch cố định cho mỗi lần rút/chuyển tiền

    /**
     * Khởi tạo tài khoản ngân hàng không có phí giao dịch ban đầu (phí = 0).
     *
     * @param id      định danh ví
     * @param name    tên tài khoản/ngân hàng, không được rỗng
     * @param balance số dư ban đầu (>= 0)
     * @param userId  id của người dùng sở hữu ví, phải > 0
     */
    public BankAccount(String id, String name, double balance, int userId) {
        this(id, name, balance, 0.0, userId);
    }

    /**
     * Khởi tạo tài khoản ngân hàng với phí giao dịch xác định.
     *
     * @param id             định danh ví
     * @param name           tên tài khoản/ngân hàng, không được rỗng
     * @param balance        số dư ban đầu (>= 0)
     * @param transactionFee phí giao dịch cố định (>= 0)
     * @param userId         id của người dùng sở hữu ví, phải > 0
     */
    public BankAccount(String id, String name, double balance, double transactionFee, int userId) {
        super(id, name, balance, WalletType.BANK, userId);
        setTransactionFee(transactionFee);
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    @Override
    public void withdraw(double amount) {
        double totalDeduction = amount + transactionFee;
        // Dùng epsilon để tránh sai số float khi rút sát/đúng số dư hiện có
        if (totalDeduction > getBalance() + CurrencyUtils.EPSILON) {
            throw new InsufficientFundsException(getBalance(), totalDeduction);
        }
        setBalance(getBalance() - totalDeduction);
    }
}