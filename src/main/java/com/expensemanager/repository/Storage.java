package com.expensemanager.repository;

import java.io.IOException;
import java.util.List;

/**
 * Interface chung cho việc đọc/ghi một danh sách đối tượng xuống ổ cứng.
 * Repository/Service chỉ phụ thuộc vào interface này (Dependency Inversion),
 * không quan tâm dữ liệu được lưu dưới dạng CSV hay JSON.
 *
 * @param <T> kiểu đối tượng cần lưu trữ (Wallet, Transaction, Category, Budget...)
 */
public interface Storage<T> {

    /**
     * Đọc toàn bộ dữ liệu từ file.
     * Nếu file chưa tồn tại, trả về danh sách rỗng (không ném lỗi).
     *
     * @param filePath đường dẫn file dữ liệu
     * @return danh sách đối tượng đọc được
     * @throws IOException nếu có lỗi khi đọc file (file hỏng, sai định dạng...)
     */
    List<T> load(String filePath) throws IOException;

    /**
     * Ghi toàn bộ danh sách xuống file (ghi đè dữ liệu cũ).
     *
     * @param filePath đường dẫn file dữ liệu
     * @param data     danh sách đối tượng cần ghi
     * @throws IOException nếu có lỗi khi ghi file
     */
    void save(String filePath, List<T> data) throws IOException;
}