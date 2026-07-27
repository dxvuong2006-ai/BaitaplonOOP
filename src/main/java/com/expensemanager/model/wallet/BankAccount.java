package com.expensemanager.model.wallet;

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
     */
    public BankAccount(int id, String name, double balance) {
        this(id, name, balance, 0.0);
    }

    /**
     * Khởi tạo tài khoản ngân hàng với phí giao dịch xác định.
     *
     * @param id             định danh ví
     * @param name           tên tài khoản/ngân hàng, không được rỗng
     * @param balance        số dư ban đầu (>= 0)
     * @param transactionFee phí giao dịch cố định (>= 0)
     */
    public BankAccount(int id, String name, double balance, double transactionFee) {
        super(id, name, balance, WalletType.BANK);
        setTransactionFee(transactionFee);
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        if (transactionFee < 0) {
            throw new IllegalArgumentException("Phí giao dịch không được âm.");
        }
        this.transactionFee = transactionFee;
    }

    /**
     * Rút tiền từ tài khoản ngân hàng.
     * Đa hình: Số tiền bị trừ = số tiền rút + phí giao dịch cố định.
     *
     * @param amount số tiền cần rút (phải > 0)
     * @throws IllegalArgumentException nếu amount <= 0 hoặc số dư không đủ (mức rút + phí)
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0.");
        }
        double totalDeduction = amount + transactionFee;
        // Dùng epsilon để tránh sai số float khi rút sát/đúng số dư hiện có
        if (totalDeduction > getBalance() + CurrencyUtils.EPSILON) {
            throw new IllegalArgumentException("Tài khoản ngân hàng không đủ số dư để thực hiện rút tiền và thanh toán phí giao dịch.");
        }
        setBalance(getBalance() - totalDeduction);
    }
}