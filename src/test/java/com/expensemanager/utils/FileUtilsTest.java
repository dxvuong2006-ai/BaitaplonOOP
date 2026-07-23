package com.expensemanager.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lop kiem thu tu dong (Unit Test) cho FileUtils dung JUnit 5.
 */
class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Test private constructor ngat khoi tao doi tuong qua Reflection")
    void testPrivateConstructor() throws Exception {
        Constructor<FileUtils> constructor = FileUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance,
                "Khong the khoi tao Utility Class tu ben ngoai"
        );

        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("Utility class khong the khoi tao!", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Test kiem tra tap tin ton tai (fileExists)")
    void testFileExists() throws Exception {
        // 1. DUONG DAN RONG HOAC NULL
        assertFalse(FileUtils.fileExists(null));
        assertFalse(FileUtils.fileExists(""));
        assertFalse(FileUtils.fileExists("   "));

        // 2. FILE KHONG TON TAI
        Path nonExistentFile = tempDir.resolve("non_existent.csv");
        assertFalse(FileUtils.fileExists(nonExistentFile.toString()));

        // 3. FILE THU CUM (KHONG PHAI REGULAR FILE)
        assertTrue(FileUtils.fileExists(tempDir.toString()) == false);

        // 4. FILE THUC SU TON TAI
        Path realFile = tempDir.resolve("test_exists.txt");
        Files.createFile(realFile);
        assertTrue(FileUtils.fileExists(realFile.toString()));
    }

    @Test
    @DisplayName("Test dam bao thu muc cha ton tai (ensureParentDirectoryExists)")
    void testEnsureParentDirectoryExists() throws Exception {
        // Test loi khi duong dan rui/null
        assertThrows(IllegalArgumentException.class, () -> FileUtils.ensureParentDirectoryExists(null));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.ensureParentDirectoryExists(""));

        // Test tao thu muc cha long nhau
        Path nestedPath = tempDir.resolve("subfolder/data/transactions.csv");
        FileUtils.ensureParentDirectoryExists(nestedPath.toString());

        assertTrue(Files.exists(nestedPath.getParent()));
    }

    @Test
    @DisplayName("Test lay phan mo rong cua file (getFileExtension)")
    void testGetFileExtension() {
        assertEquals("", FileUtils.getFileExtension(null));
        assertEquals("", FileUtils.getFileExtension(""));
        assertEquals("csv", FileUtils.getFileExtension("transactions.csv"));
        assertEquals("json", FileUtils.getFileExtension("config/settings.json"));
        assertEquals("gz", FileUtils.getFileExtension("backup.tar.gz"));
        assertEquals("", FileUtils.getFileExtension(".gitignore"));
        assertEquals("", FileUtils.getFileExtension("filename_without_extension"));
    }

    @Test
    @DisplayName("Test kiem tra dung luong file (getFileSize)")
    void testGetFileSize() throws Exception {
        // File khong ton tai tra ve 0L
        assertEquals(0L, FileUtils.getFileSize(tempDir.resolve("unknown.txt").toString()));

        // File co noi dung
        Path sampleFile = tempDir.resolve("sample.txt");
        String content = "Hello Expense Manager!";
        Files.writeString(sampleFile, content);

        assertEquals(content.getBytes().length, FileUtils.getFileSize(sampleFile.toString()));
    }

    @Test
    @DisplayName("Test xoa file (deleteFile)")
    void testDeleteFile() throws Exception {
        // Xoa file khong ton tai tra ve false
        assertFalse(FileUtils.deleteFile(tempDir.resolve("no_file.txt").toString()));

        // Xoa file dang ton tai
        Path fileToDelete = tempDir.resolve("to_delete.txt");
        Files.createFile(fileToDelete);
        assertTrue(Files.exists(fileToDelete));

        boolean deleted = FileUtils.deleteFile(fileToDelete.toString());
        assertTrue(deleted);
        assertFalse(Files.exists(fileToDelete));
    }
}
