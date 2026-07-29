package com.expensemanager.model.wallet;

import com.expensemanager.model.enums.WalletType;
import com.expensemanager.utils.CurrencyUtils;

/**
 * Lớp đại diện cho Ví điện tử (EWallet).
 * Kế thừa từ lớp trừu tượng Wallet.
 * Thể hiện tính đa hình qua phương thức withdraw(), bao gồm logic tính phí giao dịch theo tỉ lệ phần trăm.
 */
public class EWallet extends Wallet {
    private double feePercent; // Tỉ lệ phí giao dịch theo % (ví dụ: 1.5 nghĩa là 1.5%)

    /**
     * Khởi tạo ví điện tử mặc định không tính phí (% phí = 0).
     *
     * @param id      định danh ví
     * @param name    tên ví điện tử, không được rỗng
     * @param balance số dư ban đầu (>= 0)
     */
    public EWallet(int id, String name, double balance) {
        this(id, name, balance, 0.0);
    }

    /**
     * Khởi tạo ví điện tử với tỉ lệ phí giao dịch.
     *
     * @param id         định danh ví
     * @param name       tên ví điện tử, không được rỗng
     * @param balance    số dư ban đầu (>= 0)
     * @param feePercent tỉ lệ phí giao dịch tính theo % (>= 0)
     */
    public EWallet(int id, String name, double balance, double feePercent) {
        super(id, name, balance, WalletType.EWALLET);
        setFeePercent(feePercent);
    }

    public double getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(double feePercent) {
        if (feePercent < 0) {
            throw new IllegalArgumentException("Tỉ lệ phí giao dịch không được âm.");
        }
        this.feePercent = feePercent;
    }

    /**
     * Rút tiền từ ví điện tử.
     * Đa hình: Phí rút tiền = amount * (feePercent / 100.0).
     * Tổng số tiền bị trừ khỏi ví = amount + phí rút tiền.
     *
     * @param amount số tiền cần rút (phải > 0)
     * @throws IllegalArgumentException nếu amount <= 0 hoặc không đủ số dư
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0.");
        }
        double fee = amount * (feePercent / 100.0);
        double totalDeduction = amount + fee;
        // Dùng epsilon để tránh sai số float khi rút sát/đúng số dư hiện có
        if (totalDeduction > getBalance() + CurrencyUtils.EPSILON) {
            throw new IllegalArgumentException("Ví điện tử không đủ số dư để thực hiện rút tiền và thanh toán phí.");
        }
        setBalance(getBalance() - totalDeduction);
    }
}