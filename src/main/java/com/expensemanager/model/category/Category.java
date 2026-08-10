package com.expensemanager.model.category;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import java.util.Objects;

/**
 * Danh mục chi tiêu/thu nhập, dùng để phân loại các giao dịch (ví dụ: Ăn uống, Di chuyển).
 */
public class Category {

    private String id;
    private String name;
    private String description;
    private int userId;

    /** Khởi tạo một danh mục. */
    public Category(String id, String name, String description, int userId) {
        if (id == null || id.isBlank()) {
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

    /** Lấy định danh của danh mục. */
    public String getId() {
        return id;
    }

    /** Gán lại định danh cho danh mục. */
    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new EmptyFieldException(FieldType.ID);
        }
        this.id = id;
    }

    /** Lấy tên danh mục. */
    public String getName() {
        return name;
    }

    /** Đổi tên danh mục. */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        this.name = name;
    }

    /** Lấy mô tả chi tiết của danh mục. */
    public String getDescription() {
        return description;
    }

    /** Cập nhật mô tả cho danh mục. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Lấy id của người dùng sở hữu danh mục. */
    public int getUserId() {
        return userId;
    }

    /** Gán lại chủ sở hữu cho danh mục. */
    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        this.userId = userId;
    }

    /** So sánh hai danh mục có cùng định danh (id) hay không. */
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

    /** Sinh mã băm dựa trên id, khớp với logic của {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** Biểu diễn danh mục dưới dạng chuỗi dễ đọc, phục vụ debug/log. */
    @Override
    public String toString() {
        return "Category{id= " + id + ", name= " + name + "}";
    }
}