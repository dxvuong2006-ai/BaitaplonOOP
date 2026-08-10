package com.expensemanager.model.transaction;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.wallet.Wallet;
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
    private int userId;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public Transaction() {}

    /** Khởi tạo một giao dịch. */
    public Transaction(String id, double amount, LocalDate date, String note,
                       Category category, Wallet wallet, int userId) {
        setId(id);
        setAmount(amount);
        setDate(date);
        setNote(note);
        setCategory(category);
        setWallet(wallet);
        setUserId(userId);
    }

    // --- PHƯƠNG THỨC TRỪU TƯỢNG ---

    /** Trả về loại giao dịch (Thu hoặc Chi). */
    public abstract TransactionType getType();

    /** Trả về số tiền có dấu, dương nếu là thu, âm nếu là chi. */
    public abstract double getSignedAmount();

    /** Lấy định danh giao dịch. */
    public String getId() {
        return id;
    }

    /** Gán lại định danh cho giao dịch. */
    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    /** Lấy số tiền giao dịch. */
    public double getAmount() {
        return amount;
    }

    /** Gán lại số tiền giao dịch. */
    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
        this.amount = amount;
    }

    /** Lấy ngày giao dịch. */
    public LocalDate getDate() {
        return date;
    }

    /** Gán lại ngày giao dịch. */
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        this.date = date;
    }

    /** Lấy ghi chú của giao dịch. */
    public String getNote() {
        return note;
    }

    /** Gán lại ghi chú cho giao dịch. */
    public void setNote(String note) {
        this.note = note;
    }

    /** Lấy danh mục áp dụng cho giao dịch. */
    public Category getCategory() {
        return category;
    }

    /** Gán lại danh mục cho giao dịch. */
    public void setCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        this.category = category;
    }

    /** Lấy ví thực hiện giao dịch. */
    public Wallet getWallet() {
        return wallet;
    }

    /** Gán lại ví thực hiện giao dịch. */
    public void setWallet(Wallet wallet) {
        if (wallet == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        this.wallet = wallet;
    }

    /** Lấy id của người dùng sở hữu giao dịch. */
    public int getUserId() {
        return userId;
    }

    /** Gán lại chủ sở hữu cho giao dịch. */
    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        this.userId = userId;
    }
}