package com.expensemanager.factory.storage;

import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.model.transaction.TransactionRecord;

import java.util.List;
import java.time.LocalDate;
import java.util.function.Function;

public class TransactionStorageFactory extends AbstractStorageFactory<TransactionRecord> {

    public TransactionStorageFactory(StorageType storageType) {
        super(storageType);
    }

    @Override
    protected TypeToken<List<TransactionRecord>> getTypeToken() {
        return new TypeToken<List<TransactionRecord>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{
                "id",
                "amount",
                "date",
                "note",
                "categoryId",
                "walletId",
                "type",
                "extraField",
                "period"
        };
    }

    @Override
    // Có chỉnh sửa lại m đọc lại đi.
    protected Function<TransactionRecord, String[]> getSerializer() {
        return record -> new String[]{
                record.getId(),
                String.valueOf(record.getAmount()),
                DateUtils.formatDate(record.getDate()),
                record.getNote() == null ? "" : record.getNote(),
                record.getCategoryId() == null ? "" : record.getCategoryId(),
                record.getWalletId() == null ? "" : record.getWalletId(),
                record.getType(),
                record.getExtraField() == null ? "" : record.getExtraField(),
                record.getPeriod() == null ? "" : record.getPeriod()
        };
    }

    @Override
    // Có chỉnh sửa lại m đọc lại đi.
    protected Function<String[], TransactionRecord> getDeserializer() {
        return row -> {
            String id = row[0];
            double amount = Double.parseDouble(row[1]);
            LocalDate date = DateUtils.parseDate(row[2]);
            String note = row.length > 3 ? row[3] : "";
            String categoryId = row.length > 4 ? row[4] : "";
            String walletId = row.length > 5 ? row[5] : "";
            String type = row.length > 6 ? row[6] : "";
            String extraField = row.length > 7 ? row[7] : "";
            String period = row.length > 8 ? row[8] : "";

            return new TransactionRecord(id, amount, date, note, categoryId,
                    walletId, type, extraField, period);
        };
    }
}
