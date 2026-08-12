package com.expensemanager.factory.storage;

import com.expensemanager.model.enums.RecurringExecutionStatus;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.transaction.RecurringExecution;
import com.google.gson.reflect.TypeToken;
import com.expensemanager.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/** Factory quản lý storage của lịch sử thực thi giao dịch định kỳ. */
public class RecurringExecutionStorageFactory
        extends AbstractStorageFactory<RecurringExecution> {

    /** Khởi tạo RecurringExecutionStorageFactory. */
    public RecurringExecutionStorageFactory(StorageType storageType) {
        super(storageType);
    }

    /** Cung cấp TypeToken cho JSON storage. */
    @Override
    protected TypeToken<List<RecurringExecution>> getTypeToken() {
        return new TypeToken<List<RecurringExecution>>() {
        };
    }

    /** Cung cấp header cho CSV storage. */
    @Override
    protected String[] getCsvHeader() {
        return new String[]{
                "id",
                "recurringExpenseId",
                "userId",
                "dueDate",
                "amount",
                "status",
                "transactionId"
        };
    }

    /** Chuyển RecurringExecution thành dữ liệu CSV. */
    @Override
    protected Function<RecurringExecution, String[]> getSerializer() {
        return execution -> new String[]{
                execution.getId(),
                execution.getRecurringExpenseId(),
                String.valueOf(execution.getUserId()),
                execution.getDueDate() == null ? "" : DateUtils.formatDate(execution.getDueDate()),
                String.valueOf(execution.getAmount()),
                execution.getStatus() == null ? "" : execution.getStatus().name(),
                execution.getTransactionId() == null ? "" : execution.getTransactionId()
        };
    }

    /** Chuyển dữ liệu CSV thành RecurringExecution. */
    @Override
    protected Function<String[], RecurringExecution> getDeserializer() {
        return row -> {
            if (row == null || row.length < 6) {
                return null;
            }
            String id = row[0].trim();
            String recurringExpenseId = row[1].trim();
            int userId = parseUserId(row);
            LocalDate dueDate = parseDateSafely(row.length > 3 ? row[3] : "");
            double amount = parseAmount(row);
            RecurringExecutionStatus status = parseStatus(row);
            String transactionId = row.length > 6 && !row[6].trim().isEmpty()
                    ? row[6].trim()
                    : null;
            return new RecurringExecution(
                    id,
                    recurringExpenseId,
                    userId,
                    dueDate,
                    amount,
                    status,
                    transactionId
            );
        };
    }

    /** Parse mã người dùng an toàn. */
    private int parseUserId(String[] row) {
        if (row.length <= 2 || row[2].trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(row[2].trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Parse số tiền an toàn. */
    private double parseAmount(String[] row) {
        if (row.length <= 4 || row[4].trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(row[4].trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** Parse trạng thái thực thi. */
    private RecurringExecutionStatus parseStatus(String[] row) {
        if (row.length <= 5 || row[5].trim().isEmpty()) {
            return RecurringExecutionStatus.FAILED;
        }
        try {
            return RecurringExecutionStatus.valueOf(
                    row[5].trim()
            );
        } catch (IllegalArgumentException e) {
            return RecurringExecutionStatus.FAILED;
        }
    }

    /** Parse ngày an toàn. */
    private LocalDate parseDateSafely(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }
        String cleanDate = rawDate.trim();
        try {
            return DateUtils.parseDate(cleanDate);
        } catch (Exception e) {
            try {
                return LocalDate.parse(cleanDate);
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}

