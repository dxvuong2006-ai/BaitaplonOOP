package com.expensemanager.model.budget;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.Period;
import com.expensemanager.utils.CurrencyUtils;
import java.util.Objects;

/**
 * Lớp đại diện cho Ngân sách (Budget) chi tiêu trong hệ thống.
 * Giúp người dùng đặt hạn mức chi tiêu cho từng danh mục trong một chu kỳ xác định.
 */
public class Budget {
    private String id;
    private Category category;
    private double limitAmount;
    private Period period;
    private int userId;

    public Budget() {}

    /**
     * Khởi tạo một ngân sách.
     *
     * @param id          định danh ngân sách (>= 0)
     * @param category    danh mục áp dụng ngân sách (có thể null nếu áp dụng cho tổng ngân sách)
     * @param limitAmount hạn mức chi tiêu tối đa (>= 0)
     * @param period      chu kỳ áp dụng (DAILY, WEEKLY, MONTH, YEARLY)
     * @param userId      id của người dùng sở hữu ngân sách, phải > 0
     */
    public Budget(String id, Category category, double limitAmount, Period period, int userId) {
        setId(id);
        setLimitAmount(limitAmount);
        this.category = category;
        this.period = period;
        setUserId(userId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        this.userId = userId;
    }

    /**
     * Kiểm tra xem tổng số tiền đã chi tiêu có vượt quá hạn mức ngân sách hay không.
     *
     * @param spent số tiền đã chi tiêu
     * @return true nếu số tiền chi tiêu vượt quá hạn mức, ngược lại trả về false
     */
    public boolean isExceeded(double spent) {
        // Dùng epsilon để tránh sai số cộng dồn khi spent là tổng nhiều giao dịch double
        return spent > this.limitAmount + CurrencyUtils.EPSILON;
    }

    /**
     * Lấy số tiền ngân sách còn lại.
     *
     * @param spent số tiền đã chi tiêu
     * @return số tiền còn lại (limitAmount - spent)
     */
    public double getRemainingAmount(double spent) {
        return this.limitAmount - spent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Budget)) return false;
        Budget budget = (Budget) o;
        return Objects.equals(id, budget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", category=" + (category != null ? category.getName() : "Tất cả") +
                ", limitAmount=" + limitAmount +
                ", period=" + period +
                ", userId=" + userId +
                '}';
    }
}