package com.expensemanager.factory.storage;

import com.expensemanager.model.budget.Budget;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public class BudgetStorageFactory extends AbstractStorageFactory<Budget> {

    @Override
    protected TypeToken<List<Budget>> getTypeToken() {
        return new TypeToken<List<Budget>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{"id", "categoryId", "limitAmount", "period"};
    }

    @Override
    protected Function<Budget, String[]> getSerializer() {
        return budget -> new String[]{
                budget.getId(),
                budget.getCategory() != null ? budget.getCategory().getId() : "",
                String.valueOf(budget.getLimitAmount()),
                budget.getPeriod().name()
        };
    }

    @Override
    protected Function<String[], Budget> getDeserializer() {
        return row -> {
            // Budget chứa tham chiếu tới Category, do đó việc mapping
            // phải được thực hiện ở tầng Service (như ExpenseManager)
            throw new UnsupportedOperationException(
                    "Budget deserializer must be handled by ExpenseManager/BudgetService."
            );
        };
    }
}