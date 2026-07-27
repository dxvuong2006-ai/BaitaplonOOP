package com.expensemanager.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void testReadAllLines_FileNotExists() throws IOException {
        // Đường dẫn tới file không có thật trong thư mục tạm
        String nonExistentFile = tempDir.resolve("non_existent.txt").toString();

        List<String> lines = FileUtils.readAllLines(nonExistentFile);

        assertNotNull(lines);
        assertTrue(lines.isEmpty());
    }

    @Test
    void testWriteAndReadAllLines_Success() throws IOException {
        String filePath = tempDir.resolve("test_expense.txt").toString();
        List<String> expectedLines = Arrays.asList("An 100000", "Thu 500000", "Chi tieu");

        // Ghi dữ liệu vào file
        FileUtils.writeAllLines(filePath, expectedLines);

        // Đọc dữ liệu từ file
        List<String> actualLines = FileUtils.readAllLines(filePath);

        assertEquals(expectedLines, actualLines);
    }

    @Test
    void testWriteAllLines_AutoCreateParentDirectories() throws IOException {
        // Tạo đường dẫn có thư mục con chưa tồn tại
        Path subDir = tempDir.resolve("sub/folder/data.txt");
        String filePath = subDir.toString();

        List<String> lines = Collections.singletonList("Test nested directory");

        // Ghi file nên tự động tạo các thư mục cha
        assertDoesNotThrow(() -> FileUtils.writeAllLines(filePath, lines));

        // Kiểm tra xem file và nội dung đã được ghi thành công chưa
        assertTrue(Files.exists(subDir));
        List<String> actualLines = FileUtils.readAllLines(filePath);
        assertEquals(lines, actualLines);
    }
}
