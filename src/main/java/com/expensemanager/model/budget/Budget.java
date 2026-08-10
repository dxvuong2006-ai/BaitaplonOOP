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

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public Budget() {}

    /**
     * Khởi tạo một ngân sách.
     *
     * @param id định danh ngân sách (>= 0)
     * @param category danh mục áp dụng ngân sách (có thể null nếu áp dụng cho tổng ngân sách)
     * @param limitAmount hạn mức chi tiêu tối đa (>= 0)
     * @param period chu kỳ áp dụng (DAILY, WEEKLY, MONTH, YEARLY)
     * @param userId id của người dùng sở hữu ngân sách, phải > 0
     */
    public Budget(String id, Category category, double limitAmount, Period period, int userId) {
        setId(id);
        setLimitAmount(limitAmount);
        this.category = category;
        this.period = period;
        setUserId(userId);
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
     * @param id định danh mới, không được null
     * @throws EmptyFieldException nếu id null
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Lấy danh mục áp dụng ngân sách.
     *
     * @return danh mục áp dụng, có thể null nếu áp dụng cho tổng ngân sách
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Gán lại danh mục áp dụng cho ngân sách.
     *
     * @param category danh mục mới, không được null
     * @throws EmptyFieldException nếu category null
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Lấy hạn mức chi tiêu tối đa của ngân sách.
     *
     * @return hạn mức chi tiêu hiện tại
     */
    public double getLimitAmount() {
        return limitAmount;
    }

    /**
     * Cập nhật hạn mức chi tiêu tối đa.
     *
     * @param limitAmount hạn mức mới, phải >= 0
     * @throws NegativeValueException nếu limitAmount âm
     */
    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    /**
     * Lấy chu kỳ áp dụng của ngân sách.
     *
     * @return chu kỳ áp dụng (DAILY, WEEKLY, MONTH, YEARLY)
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Gán lại chu kỳ áp dụng cho ngân sách.
     *
     * @param period chu kỳ mới, không được null
     * @throws EmptyFieldException nếu period null
     */
    public void setPeriod(Period period) {
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
     * @param userId id người dùng mới, phải > 0
     * @throws EmptyFieldException nếu userId không hợp lệ
     */
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

    /**
     * So sánh hai ngân sách có cùng định danh (id) hay không.
     *
     * @param o đối tượng cần so sánh
     * @return true nếu cùng id, ngược lại false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Budget)) {
            return false;
        }
        Budget budget = (Budget) o;
        return Objects.equals(id, budget.id);
    }

    /**
     * Sinh mã băm dựa trên id, khớp với logic của {@link #equals(Object)}.
     *
     * @return mã băm của ngân sách
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Biểu diễn ngân sách dưới dạng chuỗi dễ đọc, phục vụ debug/log.
     *
     * @return chuỗi mô tả ngân sách
     */
    @Override
    public String toString() {
        return "Budget{"
                + "id=" + id
                + ", category=" + (category != null ? category.getName() : "Tất cả")
                + ", limitAmount=" + limitAmount
                + ", period=" + period
                + ", userId=" + userId
                + '}';
    }
}