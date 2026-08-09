package com.expensemanager.model.category;

import java.util.Objects;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.exception.EmptyFieldException;

/**
 * Danh mục chi tiêu/thu nhập, dùng để phân loại các giao dịch (ví dụ: Ăn uống, Di chuyển).
 */
public class Category {

    private String id;
    private String name;
    private String description;
    private int userId;

    /**
     * Khởi tạo một danh mục.
     *
     * @param id          định danh danh mục, phải >= 0
     * @param name        tên danh mục, không được rỗng
     * @param description mô tả chi tiết, có thể để trống
     * @param userId      id của người dùng sở hữu danh mục, phải > 0
     * @throws IllegalArgumentException nếu id âm, name rỗng/null, hoặc userId không hợp lệ
     */
    public Category(String id, String name, String description, int userId) {
        if(id==null || id.isBlank()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        this.id = id;
        this.name = name;
        this.description = description;
        setUserId(userId);
    }

    /**
     * Lấy định danh của danh mục.
     *
     * @return id của danh mục
     */
    public String getId() {
        return id;
    }

    /**
     * Gán lại định danh cho danh mục.
     *
     * @param id định danh mới, phải >= 0
     * @throws IllegalArgumentException nếu id âm
     */
    public void setId(String id) {
        if(id==null || id.isBlank()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    /**
     * Lấy tên danh mục.
     *
     * @return tên danh mục
     */
    public String getName() {
        return name;
    }

    /**
     * Đổi tên danh mục.
     *
     * @param name tên danh mục mới, không được rỗng
     * @throws IllegalArgumentException nếu name rỗng hoặc null
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        this.name = name;
    }

    /**
     * Lấy mô tả chi tiết của danh mục.
     *
     * @return mô tả danh mục, có thể null hoặc rỗng
     */
    public String getDescription() {
        return description;
    }

    /**
     * Cập nhật mô tả cho danh mục.
     *
     * @param description mô tả mới, có thể để trống hoặc null
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Lấy id của người dùng sở hữu danh mục.
     *
     * @return userId của chủ sở hữu
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gán lại chủ sở hữu cho danh mục.
     *
     * @param userId id người dùng mới, phải > 0
     * @throws IllegalArgumentException nếu userId không hợp lệ
     */
    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        this.userId = userId;
    }

    /**
     * So sánh hai danh mục có cùng định danh (id) hay không.
     *
     * @param o đối tượng cần so sánh
     * @return true nếu cùng id, ngược lại false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        Category other = (Category) o;
        return Objects.equals(id, other.id);
    }

    /**
     * Sinh mã băm dựa trên id, khớp với logic của {@link #equals(Object)}.
     *
     * @return mã băm của danh mục
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Biểu diễn danh mục dưới dạng chuỗi dễ đọc, phục vụ debug/log.
     *
     * @return chuỗi mô tả danh mục
     */
    @Override
    public String toString() {
        return "Category{id= " + id + ", name= " + name + "}";
    }
}