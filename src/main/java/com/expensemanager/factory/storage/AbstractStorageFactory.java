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

/**
 * Lớp trừu tượng theo mẫu Template Method, định nghĩa luồng khởi tạo và thao tác
 * lưu trữ (load/save) chung cho mọi loại dữ liệu {@code T}. Lớp con chỉ cần cung
 * cấp cấu hình đặc thù (TypeToken, header CSV, serializer/deserializer).
 *
 * @param <T> kiểu dữ liệu được lưu trữ
 */
public abstract class AbstractStorageFactory<T> {

    private final StorageType storageType;
    private final Storage<T> storage;

    protected AbstractStorageFactory(StorageType storageType) {
        this.storageType = storageType;
        this.storage = createStorage(storageType);
    }

    /**
     * Khởi tạo đối tượng {@link Storage} tương ứng với {@code type} được truyền vào.
     *
     * @param type định dạng lưu trữ (JSON, CSV)
     * @return đối tượng {@link Storage} tương ứng với định dạng được chỉ định
     * @throws EmptyFieldException nếu {@code type} là null
     */
    public Storage<T> createStorage(StorageType type) {
        if (type == null) {
            throw new EmptyFieldException(FieldType.STORAGETYPE);
        }

        return switch (type) {
            // Nhờ lớp con cung cấp TypeToken (vì Java bị Type Erasure)
            case JSON -> new JsonStorage<>(getTypeToken());

            // Nhờ lớp con cung cấp cấu hình CSV đặc thù
            case CSV -> new CsvStorage<>(getCsvHeader(), getSerializer(), getDeserializer());
        };
    }

    /**
     * Tải danh sách dữ liệu từ đường dẫn tương ứng với {@code filePath}.
     *
     * @param filePath đường dẫn logic đến file dữ liệu
     * @return danh sách dữ liệu đã tải
     * @throws RuntimeException nếu xảy ra lỗi khi đọc file
     */
    public List<T> load(FilePath filePath) {
        try {
            return storage.load(filePath.getFullPath(storageType));
        } catch (IOException e) {
            throw new RuntimeException("Không thể tải dữ liệu.", e);
        }
    }

    /**
     * Lưu danh sách dữ liệu vào đường dẫn tương ứng với {@code filePath}.
     *
     * @param filePath đường dẫn logic đến file dữ liệu
     * @param data danh sách dữ liệu cần lưu
     * @throws RuntimeException nếu xảy ra lỗi khi ghi file
     */
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