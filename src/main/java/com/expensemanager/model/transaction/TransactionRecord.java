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

    /** Constructor. */
    public TransactionRecord(){}

    /** Constructor. */
    public TransactionRecord(String id, double amount, LocalDate date, String note,
                             String categoryId, String walletId, String type,
                             String extraField, String period, int userId) {
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
        this.nextDueDate = null;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getUserId() {
        return userId;
    }

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
