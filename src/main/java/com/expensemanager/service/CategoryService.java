package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryService {

    private final List<Category> categories;

    public CategoryService() {
        categories = new ArrayList<>();
    }

    /** Thêm loại. */
    public void addCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        if (findCategoryByName(category.getName()) != null) {
            throw new DuplicateEntityException("Danh mục", category.getName());
        }
        categories.add(category);
    }

    /** Xóa loại. */
    public void removeCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        categories.remove(category);
    }

    /** Cập nhật danh mục. */
    public void updateCategory(Category oldCategory, Category newCategory) {
        ValidationService.validateCategory(oldCategory);
        ValidationService.validateCategory(newCategory);
        Category existed = findCategoryByName(newCategory.getName());
        if (existed != null && existed != oldCategory) {
            throw new DuplicateEntityException("Danh mục", newCategory.getName());
        }
        oldCategory.setName(newCategory.getName());
        oldCategory.setDescription(newCategory.getDescription());
    }

    /** Tìm theo ID. */
    public Category findCategoryById(String id) {
        for (Category category : categories) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

    /** Tìm kiếm loại theo tên. */
    public Category findCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name.trim())) {
                return category;
            }
        }
        return null;
    }

    /** Trả về danh sách loại. */
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }
}
