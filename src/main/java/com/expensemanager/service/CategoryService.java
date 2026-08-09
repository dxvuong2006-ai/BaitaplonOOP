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
    public void addCategory(Category category) {
        ValidationService.validateCategory(category);
        if (findCategoryById(category.getId()) != null) {
            throw new DuplicateEntityException(
                    "Ví", "mã " + category.getId());
        }
        if (findCategoryByName(category.getName()) != null) {
            throw new DuplicateEntityException("Danh mục", category.getName());
        }
        categories.add(category);
        save();
    }

    /** Xóa loại. */
    public void removeCategory(Category category) {
        ValidationService.validateCategory(category);
        categories.remove(category);
        save();
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
        save();
    }

    /** Tìm theo ID. */
    public Category findCategoryById(String id) {
        for (Category category : categories) {
            if (category.getId().equals(id)) {
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
