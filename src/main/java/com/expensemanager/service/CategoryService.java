package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.factory.storage.CategoryStorageFactory;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.FilePath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryService {

    private final CategoryStorageFactory storageFactory;
    private List<Category> categories = new ArrayList<>();

    public CategoryService(CategoryStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
        load();
    }

    public void load() {
        categories.clear();
        categories.addAll(storageFactory.load(FilePath.CATEGORY));
    }

    public void save() {
        storageFactory.save(FilePath.CATEGORY, categories);
    }

    /** Thêm loại. */
    public void addCategory(Category category, int userId) {
        ValidationService.validateCategory(category);
        category.setUserId(userId);
        if (findCategoryById(category.getId(), userId) != null) {
            throw new DuplicateEntityException(
                    "Danh mục", "mã " + category.getId());
        }
        ValidationService.validateCategoryName(getCategories(userId), category.getName());
        categories.add(category);
        save();
    }

    /** Xóa loại. */
    public void removeCategory(Category category, int userId) {
        ValidationService.validateCategory(category);
        Category existing = findCategoryById(category.getId(), userId);
        ValidationService.validateCategory(existing);
        categories.remove(existing);
        save();
    }

    /** Cập nhật danh mục. */
    public void updateCategory(Category oldCategory, Category newCategory, int userId) {
        ValidationService.validateCategory(oldCategory);
        ValidationService.validateCategory(newCategory);
        Category existingOld = findCategoryById(oldCategory.getId(), userId);
        ValidationService.validateCategory(existingOld);
        Category existing = findCategoryByName(newCategory.getName(), userId);
        if (existing != null && existing != oldCategory) {
            throw new DuplicateEntityException("Danh mục", newCategory.getName());
        }
        oldCategory.setName(newCategory.getName());
        oldCategory.setDescription(newCategory.getDescription());
        oldCategory.setUserId(newCategory.getUserId());
        save();
    }

    /** Tìm theo ID. */
    public Category findCategoryById(String id, int userId) {
        for (Category category : categories) {
            if (category.getId().equals(id) && category.getUserId() == userId) {
                return category;
            }
        }
        return null;
    }

    /** Tìm kiếm loại theo tên. */
    public Category findCategoryByName(String name, int userId) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Category category : getCategories(userId)) {
            if (category.getName().equalsIgnoreCase(name.trim()) && category.getUserId() == userId) {
                return category;
            }
        }
        return null;
    }

    /** Trả về danh sách loại. */
    public List<Category> getCategories(int userId) {
        return categories.stream()
                .filter(category -> category.getUserId() == userId)
                .toList();
    }
}
