package com.expensemanager.model.budget;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.utils.CurrencyUtils;
import java.util.Objects;

/**
 * Lớp đại diện cho Ngân sách (Budget) chi tiêu trong hệ thống.
 * Giúp người dùng đặt hạn mức chi tiêu cho từng danh mục trong một chu kỳ xác định.
 */
public class Budget {
    private int id;
    private Category category;
    private double limitAmount; 
    private Period period;

    public Budget() {}

    /**
     * Khởi tạo một ngân sách.
     *
     * @param id          định danh ngân sách (>= 0)
     * @param category    danh mục áp dụng ngân sách (có thể null nếu áp dụng cho tổng ngân sách)
     * @param limitAmount hạn mức chi tiêu tối đa (>= 0)
     * @param period      chu kỳ áp dụng (DAILY, WEEKLY, MONTH, YEARLY)
     */
    public Budget(int id, Category category, double limitAmount, Period period) {
        setId(id);
        setLimitAmount(limitAmount);
        this.category = category;
        this.period = period;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Mã định danh ngân sách không được âm.");
        }
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
        if (limitAmount < 0) {
            throw new IllegalArgumentException("Hạn mức ngân sách không được âm.");
        }
        this.limitAmount = limitAmount;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
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
        return id == budget.id;
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
                '}';
    }
}