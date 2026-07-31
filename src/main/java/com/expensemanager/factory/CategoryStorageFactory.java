package com.expensemanager.factory;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.repository.CsvStorage;
import com.expensemanager.repository.JsonStorage;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public final class CategoryStorageFactory {

    private CategoryStorageFactory() {
        // Không cho phép khởi tạo Factory
    }

    public static Storage<Category> createStorage(StorageType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.STORAGETYPE);
        }

        switch (type) {
            case CSV:
                String[] header = {"id", "name", "description"};

                Function<Category, String[]> serializer = cat -> new String[]{
                        cat.getId(),
                        cat.getName(),
                        cat.getDescription() != null ? cat.getDescription() : ""
                };

                Function<String[], Category> deserializer = row -> {
                    String id = row[0];
                    String name = row[1];
                    String description = row.length > 2 ? row[2] : "";
                    return new Category(id, name, description);
                };

                return new CsvStorage<>(header, serializer, deserializer);

            case JSON:
                return new JsonStorage<>(new TypeToken<List<Category>>() {});

            default:
                throw new UnsupportedOperationException("Chưa hỗ trợ định dạng storage: " + type);
        }
    }
}