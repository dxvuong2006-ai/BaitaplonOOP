package com.expensemanager.factory.storage;

import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;
import com.expensemanager.model.budget.BudgetRecord;

import java.util.List;
import java.util.function.Function;

public class BudgetStorageFactory extends AbstractStorageFactory<BudgetRecord> {

    public BudgetStorageFactory(StorageType storageType) {
        super(storageType);
    }

    @Override
    protected TypeToken<List<BudgetRecord>> getTypeToken() {
        return new TypeToken<List<BudgetRecord>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{"id", "categoryId", "limitAmount", "period"};
    }

    @Override
    protected Function<BudgetRecord, String[]> getSerializer() {
        return record -> new String[]{
                record.getId(),
                record.getCategoryId() == null ? "" : record.getCategoryId(),
                String.valueOf(record.getLimitAmount()),
                record.getPeriod()
        };
    }

    @Override
    protected Function<String[], BudgetRecord> getDeserializer() {
        return row -> {
            String id = row[0];
            String categoryId = row.length > 1 ? row[1] : "";
            double limitAmount = row.length > 2 ? Double.parseDouble(row[2]) : 0.0;
            String period = row.length > 3 ? row[3] : "";

            return new BudgetRecord(id, categoryId, limitAmount, period);
        };
    }
}
