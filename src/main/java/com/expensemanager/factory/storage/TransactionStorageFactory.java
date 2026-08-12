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
                "period",
                "userId",
                "nextDueDate",
                "active"
        };
    }

    @Override
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
                record.getPeriod() == null ? "" : record.getPeriod(),
                String.valueOf(record.getUserId()),
                record.getNextDueDate() == null ? "" : DateUtils.formatDate(record.getNextDueDate()),
                String.valueOf(record.isActive())
        };
    }

    @Override
    protected Function<String[], TransactionRecord> getDeserializer() {
        return row -> {
            // 1. Kiểm tra an toàn độ dài dòng thô
            if (row == null || row.length < 3) {
                return null;
            }
            String id = row[0].trim();
            // 2. Parse số tiền an toàn (loại bỏ khoảng trắng)
            double amount = 0.0;
            try {
                amount = Double.parseDouble(row[1].trim());
            } catch (NumberFormatException ignored) {}
            // 3. Parse ngày an toàn (Xử lý dứt điểm lỗi crash)
            LocalDate date = parseDateSafely(row[2]);
            // 4. Lấy các trường còn lại với trim()
            String note = row.length > 3 ? row[3].trim() : "";
            String categoryId = row.length > 4 ? row[4].trim() : "";
            String walletId = row.length > 5 ? row[5].trim() : "";
            String type = row.length > 6 ? row[6].trim() : "";
            String extraField = row.length > 7 ? row[7].trim() : "";
            String period = row.length > 8 ? row[8].trim() : "";
            int userId = row.length > 9 ? Integer.parseInt(row[9].trim()) : 0;
            String rawNextDate = row.length > 10? row[10].trim() : "";
            LocalDate nextDueDate = rawNextDate.isEmpty()? null : parseDateSafely(rawNextDate);
            Boolean active = row.length > 11 ? Boolean.parseBoolean(row[11].trim()) : true;

            return new TransactionRecord(id, amount, date, note, categoryId,
                    walletId, type, extraField, period, userId, nextDueDate, active);
        };
    }

    /** Chuyển dạng ngày an toàn. */
    private LocalDate parseDateSafely(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return LocalDate.now();
        }

        String cleanDate = rawDate.trim();
        try {
            return DateUtils.parseDate(cleanDate);
        } catch (Exception e) {
            try {
                // Thử parse chuẩn ISO (yyyy-MM-dd) nếu DateUtils thất bại
                return LocalDate.parse(cleanDate);
            } catch (Exception ex) {
                // Giá trị mặc định an toàn để không crash luồng đọc
                return LocalDate.now();
            }
        }
    }
}
