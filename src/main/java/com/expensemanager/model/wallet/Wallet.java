package com.expensemanager.model.wallet;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.WalletType;

/**
 * Lớp trừu tượng đại diện cho một ví tiền trong hệ thống quản lý chi tiêu.
 * Các lớp con phải tự định nghĩa logic rút tiền riêng (ví dụ CashWallet, BankWallet).
 */
public abstract class Wallet {

    private String id;
    private String name;
    protected double balance;
    private final WalletType type;
    private int userId;

    /** Khởi tạo một ví tiền. */
    public Wallet(String id, String name, double balance, WalletType type, int userId) {
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
        setUserId(userId);
    }

    /** Lấy định danh của ví. */
    public String getId() {
        return id;
    }

    /** Gán lại định danh cho ví. */
    public void setId(String id) {
        if (id == null) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    /** Lấy tên ví. */
    public String getName() {
        return name;
    }

    /** Đổi tên ví. */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        this.name = name;
    }

    /** Lấy số dư hiện có của ví. */
    public double getBalance() {
        return balance;
    }

    /** Cập nhật số dư của ví. */
    public void setBalance(double balance) {
        if (balance < 0) {
            throw new NegativeValueException(FieldType.BALANCE);
        }
        this.balance = balance;
    }

    /** Lấy loại ví (CASH/BANK/EWALLET). */
    public WalletType getType() {
        return type;
    }

    /** Lấy id của người dùng sở hữu ví. */
    public int getUserId() {
        return userId;
    }

    /** Gán lại chủ sở hữu cho ví. */
    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        this.userId = userId;
    }

    /** Nạp tiền vào ví. */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        this.balance += amount;
    }

    /** Rút tiền khỏi ví, mỗi loại ví con tự định nghĩa cách xử lý riêng. */
    public abstract void withdraw(double amount);
}
