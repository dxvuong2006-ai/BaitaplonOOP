package com.expensemanager.factory;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.repository.CsvStorage;
import com.expensemanager.repository.JsonStorage;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public final class TransactionStorageFactory {

    private TransactionStorageFactory() {
        // Không cho phép khởi tạo Factory
    }

    public static Storage<Transaction> createStorage(StorageType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.STORAGETYPE);
        }

        switch (type) {
            case CSV:
                String[] header = {"id", "amount", "date", "note", "categoryId", "walletId", "type", "extraField1", "period"};

                Function<Transaction, String[]> serializer = tx -> new String[]{
                        tx.getId(),
                        String.valueOf(tx.getAmount()),
                        tx.getDate().toString(),
                        tx.getNote(),
                        tx.getCategory() != null ? tx.getCategory().getId() : "",
                        tx.getWallet() != null ? tx.getWallet().getId() : "",
                        tx.getType().name(),
                        extractTransactionExtraField(tx),
                        extractTransactionPeriod(tx)
                };

                Function<String[], Transaction> deserializer = row -> {
                    throw new UnsupportedOperationException(
                            "Transaction deserializer must be handled by TransactionService."
                    );
                };

                return new CsvStorage<>(header, serializer, deserializer);

            case JSON:
                return new JsonStorage<>(new TypeToken<List<Transaction>>() {});

            default:
                throw new UnsupportedOperationException("Chưa hỗ trợ định dạng storage: " + type);
        }
    }

    private static String extractTransactionExtraField(Transaction tx) {
        return "";
    }

    private static String extractTransactionPeriod(Transaction tx) {
        return "";
    }
}