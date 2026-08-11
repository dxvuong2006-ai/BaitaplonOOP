package com.expensemanager.factory.storage;

import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.transaction.TransactionRecord;
import com.expensemanager.utils.DateUtils;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/** Lớp factory tạo {@link} cho dữ liệu {@link TransactionRecord}. */
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
        return new String[] {
                "id",
                "amount",
                "date",
                "note",
                "categoryId",
                "walletId",
                "type",
                "extraField",
                "period",
                "userId"
        };
    }

    @Override
    protected Function<TransactionRecord, String[]> getSerializer() {
        return record -> new String[] {
                record.getId(),
                String.valueOf(record.getAmount()),
                DateUtils.formatDate(record.getDate()),
                record.getNote() == null ? "" : record.getNote(),
                record.getCategoryId() == null ? "" : record.getCategoryId(),
                record.getWalletId() == null ? "" : record.getWalletId(),
                record.getType(),
                record.getExtraField() == null ? "" : record.getExtraField(),
                record.getPeriod() == null ? "" : record.getPeriod(),
                String.valueOf(record.getUserId())
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
            } catch (NumberFormatException ignored) {
                // Giữ giá trị mặc định 0.0 nếu parse thất bại
            }

            // 3. Parse ngày an toàn (xử lý dứt điểm lỗi crash)
            LocalDate date = parseDateSafely(row[2]);

            // 4. Lấy các trường còn lại với trim()
            String note = row.length > 3 ? row[3].trim() : "";
            String categoryId = row.length > 4 ? row[4].trim() : "";
            String walletId = row.length > 5 ? row[5].trim() : "";
            String type = row.length > 6 ? row[6].trim() : "";
            String extraField = row.length > 7 ? row[7].trim() : "";
            String period = row.length > 8 ? row[8].trim() : "";
            int userId = row.length > 9 ? Integer.parseInt(row[9].trim()) : 0;

            return new TransactionRecord(
                    id, amount, date, note, categoryId, walletId, type, extraField, period, userId);
        };
    }

    /**
     * Parse chuỗi ngày tháng một cách an toàn, trả về ngày hiện tại nếu chuỗi rỗng
     * hoặc không thể parse theo mọi định dạng đã thử.
     *
     * @param rawDate chuỗi ngày tháng thô đọc từ file
     * @return đối tượng {@link LocalDate} tương ứng, hoặc ngày hiện tại nếu thất bại
     */
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