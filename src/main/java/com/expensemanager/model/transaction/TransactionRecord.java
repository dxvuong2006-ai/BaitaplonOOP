package com.expensemanager.model.transaction;

import java.time.LocalDate;

/** Lớp dữ liệu thô đại diện cho 1 transaction. */
public class TransactionRecord {
    private String id;
    private double amount;
    private LocalDate date;
    private String note;
    private String categoryId;
    private String walletId;
    private String type;
    private String extraField;
    private String period;
    private int userId;
    private LocalDate nextDueDate;
    private boolean active;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public TransactionRecord() {}

    /** Khởi tạo một bản ghi giao dịch thô. */
    public TransactionRecord(String id, double amount, LocalDate date, String note,
                             String categoryId, String walletId, String type, String extraField, String period,
                             int userId) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.type = type;
        this.extraField = extraField;
        this.period = period;
        this.userId = userId;
        this.nextDueDate = date;
        this.active = true;
    }

    /** Constructor. */
    public TransactionRecord(String id, double amount, LocalDate date, String note,
                             String categoryId, String walletId, String type,
                             String extraField, String period, int userId,
                             LocalDate nextDueDate, boolean active) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.type = type;
        this.extraField = extraField;
        this.period = period;
        this.userId = userId;
        this.nextDueDate = nextDueDate;
        this.active = active;
    }

    /** Lấy định danh giao dịch. */
    public String getId() {
        return id;
    }

    /** Gán lại định danh giao dịch. */
    public void setId(String id) {
        this.id = id;
    }

    /** Lấy số tiền giao dịch. */
    public double getAmount() {
        return amount;
    }

    /** Gán lại số tiền giao dịch. */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /** Lấy ngày giao dịch. */
    public LocalDate getDate() {
        return date;
    }

    /** Gán lại ngày giao dịch. */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** Lấy ghi chú của giao dịch. */
    public String getNote() {
        return note;
    }

    /** Gán lại ghi chú của giao dịch. */
    public void setNote(String note) {
        this.note = note;
    }

    /** Lấy id danh mục áp dụng, dạng chuỗi thô. */
    public String getCategoryId() {
        return categoryId;
    }

    /** Gán lại id danh mục áp dụng. */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /** Lấy id ví thực hiện giao dịch, dạng chuỗi thô. */
    public String getWalletId() {
        return walletId;
    }

    /** Gán lại id ví thực hiện giao dịch. */
    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    /** Lấy loại giao dịch, dạng chuỗi thô. */
    public String getType() {
        return type;
    }

    /** Gán lại loại giao dịch. */
    public void setType(String type) {
        this.type = type;
    }

    /** Lấy trường mở rộng (source hoặc paymentMethod tùy loại giao dịch). */
    public String getExtraField() {
        return extraField;
    }

    /** Gán lại trường mở rộng. */
    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

    /** Lấy chu kỳ lặp lại, dạng chuỗi thô, chỉ dùng cho giao dịch định kỳ. */
    public String getPeriod() {
        return period;
    }

    /** Gán lại chu kỳ lặp lại. */
    public void setPeriod(String period) {
        this.period = period;
    }

    /** Lấy id của người dùng sở hữu giao dịch. */
    public int getUserId() {
        return userId;
    }

    /** Gán lại chủ sở hữu cho giao dịch. */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
