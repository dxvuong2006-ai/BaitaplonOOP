package com.expensemanager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Lớp xử lý việc đọc và ghi file. */
public class FileUtils {
    /** Ngăn không cho tạo đối tượng từ bên ngoài do các phương thức đều là static. */
    private FileUtils() {}

    /** Đọc dữ liệu trong file đã tồn tại. */
    public static List<String> readAllLines(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>(); // Nếu chưa có file thì trả về 1 danh sách rỗng.
        }
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /** Viết dữ liệu vào trong file đã tồn tại. */
    public static void writeAllLines(String filePath, List<String> lines) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
    }
}
