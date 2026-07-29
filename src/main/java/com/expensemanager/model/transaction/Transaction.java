package com.expensemanager.model.transaction;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.exception.NegativeAmountException;
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
        if (id == null || id.isBlank()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new NegativeAmountException("Số tiền giao dịch phải lớn hơn 0.");
        }
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
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
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        this.category = category;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        if (wallet == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        this.wallet = wallet;
    }
}