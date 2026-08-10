package com.expensemanager.model.wallet;

import com.expensemanager.exception.InsufficientFundsException;
import com.expensemanager.exception.NegativeValueException;
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

    /** Khởi tạo tài khoản ngân hàng không có phí giao dịch ban đầu (phí = 0). */
    public BankAccount(String id, String name, double balance, int userId) {
        this(id, name, balance, 0.0, userId);
    }

    /** Khởi tạo tài khoản ngân hàng với phí giao dịch xác định. */
    public BankAccount(String id, String name, double balance, double transactionFee, int userId) {
        super(id, name, balance, WalletType.BANK, userId);
        setTransactionFee(transactionFee);
    }

    /** Lấy phí giao dịch cố định. */
    public double getTransactionFee() {
        return transactionFee;
    }

    /** Gán lại phí giao dịch cố định. */
    public void setTransactionFee(double transactionFee) {
        if (transactionFee < 0) {
            throw new NegativeValueException(FieldType.TRANSACTIONFEE);
        }
        this.transactionFee = transactionFee;
    }

    /** Rút tiền từ tài khoản ngân hàng, số tiền bị trừ gồm cả phí giao dịch cố định. */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0.");
        }
        double totalDeduction = amount + transactionFee;
        // Dùng epsilon để tránh sai số float khi rút sát/đúng số dư hiện có
        if (totalDeduction > getBalance() + CurrencyUtils.EPSILON) {
            throw new InsufficientFundsException(getBalance(), totalDeduction);
        }
        setBalance(getBalance() - totalDeduction);
    }
}