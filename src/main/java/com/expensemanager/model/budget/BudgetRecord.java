package com.expensemanager.model.budget;

/** Lớp dữ liệu thô đại diện cho 1 budget. */
public class BudgetRecord {

    private String id;
    private String categoryId;
    private double limitAmount;
    private String period;
    private int userId;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public BudgetRecord() {}

    /**
     * Khởi tạo một bản ghi ngân sách thô.
     *
     * @param id định danh ngân sách
     * @param categoryId id của danh mục áp dụng
     * @param limitAmount hạn mức chi tiêu tối đa
     * @param period chu kỳ áp dụng, ở dạng chuỗi thô
     * @param userId id của người dùng sở hữu ngân sách
     */
    public BudgetRecord(
            String id, String categoryId, double limitAmount, String period, int userId) {
        this.id = id;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.period = period;
        this.userId = userId;
    }

    /**
     * Lấy định danh của ngân sách.
     *
     * @return id của ngân sách
     */
    public String getId() {
        return id;
    }

    /**
     * Gán lại định danh cho ngân sách.
     *
     * @param id định danh mới
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Lấy id của danh mục áp dụng ngân sách.
     *
     * @return categoryId dạng chuỗi thô
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Gán lại id danh mục áp dụng.
     *
     * @param categoryId id danh mục mới, dạng chuỗi thô
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Lấy hạn mức chi tiêu tối đa.
     *
     * @return hạn mức chi tiêu hiện tại
     */
    public double getLimitAmount() {
        return limitAmount;
    }

    /**
     * Cập nhật hạn mức chi tiêu tối đa.
     *
     * @param limitAmount hạn mức mới
     */
    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    /**
     * Lấy chu kỳ áp dụng của ngân sách.
     *
     * @return chu kỳ áp dụng, dạng chuỗi thô
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Gán lại chu kỳ áp dụng cho ngân sách.
     *
     * @param period chu kỳ mới, dạng chuỗi thô
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * Lấy id của người dùng sở hữu ngân sách.
     *
     * @return userId của chủ sở hữu
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gán lại chủ sở hữu cho ngân sách.
     *
     * @param userId id người dùng mới
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}