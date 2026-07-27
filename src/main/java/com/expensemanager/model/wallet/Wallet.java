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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên ví không được để trống.");
        }
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Số dư không được âm.");
        }
        this.balance = balance;
    }

    public WalletType getType() {
        return type;
    }

    /**
     * Nạp tiền vào ví.
     *
     * @param amount số tiền nạp, phải > 0
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0.");
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