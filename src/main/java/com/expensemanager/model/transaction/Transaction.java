package com.expensemanager.model.transaction;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.exception.EmptyFieldException;
import java.time.LocalDate;

/**
 * Lớp trừu tượng quản lý thông tin chung của mọi giao dịch tài chính.
 * Cung cấp bộ khung toàn diện về Đóng gói và Kiểm tra tính hợp lệ của dữ liệu (Validation).
 * Các lớp con (Income, Expense) sẽ kế thừa và định nghĩa loại giao dịch cụ thể.
 */
public abstract class Transaction {

    private String id;
    private double amount;
    private LocalDate date;
    private String note;
    private Wallet wallet;
    private Category category;

    /**
     * Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML).
     */
    public Transaction() {}
    public Transaction(String id, double amount, LocalDate date, String note, Category category, Wallet wallet) {
        setId(id);
        setAmount(amount);
        setDate(date);
        setNote(note);
        setCategory(category);
        setWallet(wallet);
    }

    // --- PHƯƠNG THỨC TRỪU TƯỢNG ---

    /**
     * Trả về loại giao dịch (Thu hoặc Chi).
     * @return TransactionType
     */
    public abstract TransactionType getType();
    public abstract double getSignedAmount();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}