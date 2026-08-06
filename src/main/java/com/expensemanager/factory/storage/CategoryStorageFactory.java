package com.expensemanager.factory.storage;

import com.expensemanager.model.category.Category;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public class CategoryStorageFactory extends AbstractStorageFactory<Category> {

    @Override
    protected TypeToken<List<Category>> getTypeToken() {
        return new TypeToken<List<Category>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{"id", "name", "description"};
    }

    @Override
    protected Function<Category, String[]> getSerializer() {
        return cat -> new String[]{
                cat.getId(),
                cat.getName(),
                cat.getDescription() != null ? cat.getDescription() : ""
        };
    }

    @Override
    protected Function<String[], Category> getDeserializer() {
        return row -> {
            String id = row[0];
            String name = row[1];
            String description = row.length > 2 ? row[2] : "";

            // Category là object độc lập nên có thể tự tạo mới ngay tại đây
            return new Category(id, name, description);
        };
    }
}