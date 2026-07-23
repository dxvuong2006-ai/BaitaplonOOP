package com.expensemanager.model.wallet;

/**
 * Lớp trừu tượng đại diện cho một ví tiền trong hệ thống quản lý chi tiêu.
 * Các lớp con phải tự định nghĩa logic rút tiền riêng (ví dụ CashWallet, BankWallet).
 */
public abstract class wallet {

    private int id;
    private String name;
    protected double balance;
    private final WalletType type;

    /**
     * Khởi tạo một ví tiền.
     *
     * @param id định danh ví
     * @param name tên ví, không được rỗng
     * @param balance số dư ban đầu, phải >= 0
     * @param type loại ví, không được null
     */
    public Wallet(int id, String name, double balance, WalletType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên ví không được để trống.");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Số dư ban đầu không được âm.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Loại ví không được để trống.");
        }
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.type = type;
    }
