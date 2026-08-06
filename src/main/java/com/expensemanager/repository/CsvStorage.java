package com.expensemanager.repository;

import com.expensemanager.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CsvStorage bản GỘP:
 * - Giữ ý tưởng dùng Function (lambda) của AI kia để không phải viết 1 class
 *   Mapper riêng cho mỗi loại Model -> gọn hơn.
 * - Nhưng serializer/deserializer làm việc trên String[] (mảng field thô)
 *   thay vì String (dòng CSV hoàn chỉnh), rồi giao việc escape/parse cho
 *   {@link CsvUtils} -> vá lỗi "vỡ cột" khi dữ liệu chứa dấu phẩy.
 *
 * @param <T> kiểu đối tượng cần lưu trữ
 */
public class CsvStorage<T> implements Storage<T> {

    private final String[] header;
    private final Function<T, String[]> serializer;
    private final Function<String[], T> deserializer;

    /**
     * @param header       tên các cột, VD: {"id", "name", "description"}
     * @param serializer   chuyển 1 đối tượng T thành mảng field thô (chưa escape)
     * @param deserializer dựng lại đối tượng T từ mảng field thô (đã unescape)
     */
    public CsvStorage(String[] header, Function<T, String[]> serializer, Function<String[], T> deserializer) {
        this.header = header;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public List<T> load(String filePath) throws IOException {
        List<String> lines = FileUtils.readAllLines(filePath);
        List<T> result = new ArrayList<>();
        if (lines.isEmpty()) {
            return result;
        }

        // File luôn do chính Storage này ghi ra -> dòng đầu chắc chắn là header,
        // không cần so sánh nội dung, tránh trường hợp trùng ngẫu nhiên với dữ liệu thật.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.isBlank()) {
                continue;
            }
            try {
                String[] fields = CsvUtils.parseRow(line);
                T item = deserializer.apply(fields);
                if (item != null) {
                    result.add(item);
                }
            } catch (Exception e) {
                // Không để 1 dòng lỗi làm crash cả ứng dụng, nhưng vẫn ghi rõ vị trí lỗi
                // để người dùng/lập trình viên biết dòng nào trong file bị hỏng.
                System.err.println("Bỏ qua dòng " + (i + 1) + " trong " + filePath
                        + " do lỗi: " + e.getMessage());
            }
        }
        return result;
    }
    @Override
    public void save(String filePath, List<T> data) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(CsvUtils.joinRow(header));

        if (data != null) {
            for (T item : data) {
                String[] row = serializer.apply(item);
                lines.add(CsvUtils.joinRow(row));
            }
        }
        FileUtils.writeAllLines(filePath, lines);
    }
}