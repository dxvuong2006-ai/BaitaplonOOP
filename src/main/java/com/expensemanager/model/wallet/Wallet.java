package com.expensemanager.model.wallet;

import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.exception.EmptyFieldException;

/**
 * Lớp trừu tượng đại diện cho một ví tiền trong hệ thống quản lý chi tiêu.
 * Các lớp con phải tự định nghĩa logic rút tiền riêng (ví dụ CashWallet, BankWallet).
 */
public abstract class Wallet {

    private String id;
    private String name;
    protected double balance;
    private final WalletType type;

    /**
     * Khởi tạo một ví tiền.
     *
     * @param id      định danh ví, phải >= 0
     * @param name    tên ví, không được rỗng
     * @param balance số dư ban đầu, phải >= 0
     * @param type    loại ví, không được null
     * @throws IllegalArgumentException nếu id âm, name rỗng/null, balance âm, hoặc type null
     */
    public Wallet(String id, String name, double balance, WalletType type) {
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        if (balance < 0) {
            throw new NegativeValueException(FieldType.BALANCE);
        }
        if (type == null) {
            throw new EmptyFieldException(FieldType.WALLETTYPE);
        }
        setId(id);
        this.name = name;
        this.balance = balance;
        this.type = type;
    }

    /**
     * Lấy định danh của ví.
     *
     * @return id của ví
     */
    public String getId() {
        return id;
    }

    /**
     * Gán lại định danh cho ví.
     *
     * @param id định danh mới, phải >= 0
     * @throws IllegalArgumentException nếu id âm
     */
    public void setId(String id) {
        if (id == null) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    /**
     * Lấy tên ví.
     *
     * @return tên ví
     */
    public String getName() {
        return name;
    }

    /**
     * Đổi tên ví.
     *
     * @param name tên ví mới, không được rỗng
     * @throws IllegalArgumentException nếu name rỗng hoặc null
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        this.name = name;
    }

    /**
     * Lấy số dư hiện có của ví.
     *
     * @return số dư hiện tại
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Cập nhật số dư của ví. Dùng nội bộ bởi {@link #deposit(double)} và
     * bởi các lớp con khi cài đặt {@link #withdraw(double)}.
     *
     * @param balance số dư mới, phải >= 0
     * @throws IllegalArgumentException nếu balance âm
     */
    public void setBalance(double balance) {
        if (balance < 0) {
            throw new NegativeValueException(FieldType.BALANCE);
        }
        this.balance = balance;
    }

    /**
     * Lấy loại ví (CASH/BANK/EWALLET).
     *
     * @return loại ví
     */
    public WalletType getType() {
        return type;
    }

    /**
     * Nạp tiền vào ví.
     *
     * @param amount số tiền nạp, phải > 0
     * @throws IllegalArgumentException nếu amount <= 0
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        this.balance += amount;
    }

    /**
     * Rút tiền khỏi ví. Mỗi loại ví con tự định nghĩa cách xử lý riêng
     * (ví dụ CashWallet không cho rút quá số dư, BankWallet có thể cho phép thấu chi).
     *
     * @param amount số tiền cần rút, phải > 0
     */
    public abstract void withdraw(double amount);
}