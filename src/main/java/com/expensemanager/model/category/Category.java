package com.expensemanager.model.category;

import java.util.Objects;

/**
 * Danh mục chi tiêu/thu nhập, dùng để phân loại các giao dịch (ví dụ: Ăn uống, Di chuyển).
 */
public class Category {

    private int id;
    private String name;
    private String description;

    /**
     * Khởi tạo một danh mục.
     *
     * @param id định danh danh mục
     * @param name tên danh mục, không được rỗng
     * @param description mô tả chi tiết, có thể để trống
     */
    public Category(int id, String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        Category other = (Category) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "category{id=" + id + ", name='" + name + "'}";
    }
}