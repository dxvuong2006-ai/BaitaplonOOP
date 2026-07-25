package com.expensemanager.model.transaction;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.wallet.Wallet;
import java.time.LocalDate;

/** Lớp trừ tượng quản lý thông tin chung của mọi giao dịch tài chính. */
public abstract class Transaction {
    private String id;
    private double amount;
    private LocalDate date;
    private String note;
    private Wallet wallet;
    private Category category;

    public Transaction() {}
    public Transaction(String id, double amount, LocalDate date, String note, Category category, Wallet wallet) {
        this.id = id;
        setAmount(amount);
        this.date = date;
        this.note = note;
        this.category = category;
        this.wallet = wallet;
    }

    /** Phương thức trừu tượng. */
    public abstract TransactionType getType();
    public abstract double getSignedAmount();

    /** Phương thức nghiệp vụ Id. */
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /** Phương thức nghiệp vụ amount. */
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
        this.amount = amount;
    }

    /** Phương thức nghiệp vụ Date. */
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** Phương thức nghiệp vụ ghi chú. */
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    /** Phương thức nghiệp vụ Category(danh mục). */
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    /** Phương thức nghiệp vụ Wallet. */
    public Wallet getWallet() {
        return wallet;
    }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
