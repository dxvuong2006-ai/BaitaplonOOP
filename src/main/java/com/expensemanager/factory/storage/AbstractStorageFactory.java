package com.expensemanager.factory.storage;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.FilePath;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.repository.CsvStorage;
import com.expensemanager.repository.JsonStorage;
import com.expensemanager.repository.Storage;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

    public abstract class AbstractStorageFactory<T> {

        private Storage<T> storage;
        private StorageType storageType;

        protected AbstractStorageFactory(StorageType storageType) {
            this.storageType = storageType;
            this.storage = createStorage(storageType);
        }

        // Đây là "Template Method" - quyết định luồng đi chung
        public Storage<T> createStorage(StorageType type) {
            if (type == null) {
                throw new EmptyFieldException(FieldType.STORAGETYPE);
            }
            switch (type) {
                case JSON:
                    // Nhờ lớp con cung cấp TypeToken (vì Java bị Type Erasure)
                    return new JsonStorage<>(getTypeToken());
                case CSV:
                    // Nhờ lớp con cung cấp cấu hình CSV đặc thù
                    return new CsvStorage<>(getCsvHeader(), getSerializer(), getDeserializer());
                default:
                    throw new UnsupportedOperationException("Chưa hỗ trợ định dạng storage: " + type);
            }
        }

        public List<T> load(FilePath filePath) {
            try {
                return storage.load(filePath.getFullPath(storageType));
            } catch (IOException e) {
                throw new RuntimeException("Không thể tải dữ liệu. ", e);
            }
        }

        public void save(FilePath filePath, List<T> data) {
            try {
                storage.save(filePath.getFullPath(storageType), data);
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu dữ liệu.", e);
            }
        }

        public Storage<T> getStorage() {
            return storage;
        }

        // --- Các hàm abstract "bắt buộc" lớp con phải tự khai báo ---
        protected abstract TypeToken<List<T>> getTypeToken();
        protected abstract String[] getCsvHeader();
        protected abstract Function<T, String[]> getSerializer();
        protected abstract Function<String[], T> getDeserializer();
    }
