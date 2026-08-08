package com.expensemanager.model.budget;

/** Lớp dữ liệu thô đại diện cho 1 budget. */
public class BudgetRecord {

    private String id;
    private String categoryId;
    private double limitAmount;
    private String period;

    /** Constructor. */
    public BudgetRecord() {}

    /** Constructor. */
    public BudgetRecord(String id, String categoryId, double limitAmount, String period) {
        this.id = id;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.period = period;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

}
