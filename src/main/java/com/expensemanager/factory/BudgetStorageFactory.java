package com.expensemanager.factory;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.repository.CsvStorage;
import com.expensemanager.repository.JsonStorage;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public final class BudgetStorageFactory {

    private BudgetStorageFactory() {
        // Không cho phép khởi tạo Factory
    }

    public static Storage<Budget> createStorage(StorageType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.STORAGETYPE);
        }

        switch (type) {
            case CSV:
                String[] header = {"id", "categoryId", "limitAmount","period"};

                Function<Budget, String[]> serializer = budget -> new String[]{
                        budget.getId(),
                        budget.getCategory() != null ? budget.getCategory().getId() : "",
                        String.valueOf(budget.getLimitAmount()),
                        budget.getPeriod().name()
                };

                Function<String[], Budget> deserializer = row -> {
                    throw new UnsupportedOperationException(
                            "Budget deserializer must be handled by BudgetService."
                    );
                };

                return new CsvStorage<>(header, serializer, deserializer);

            case JSON:
                return new JsonStorage<>(new TypeToken<List<Budget>>() {});

            default:
                throw new UnsupportedOperationException("Chưa hỗ trợ định dạng storage: " + type);
        }
    }
}