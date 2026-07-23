package com.expensemanager.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Lop tien ich (utility class) xu ly cac thao tac doc/ghi file dung chung.
 */
public final class FileUtils {

    /** Dinh dang thoi gian dung cho ten file backup, vi du: 20260722_153045. */
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("uuuuMMdd_HHmmss");

    /** Hau to gan vao ten file backup, dat truoc phan mo rong. */
    private static final String BACKUP_SUFFIX = "_backup_";

    /** Ngan khong cho tao doi tuong tu ben ngoai. */
    private FileUtils() {
        throw new UnsupportedOperationException("Utility class khong the khoi tao!");
    }

    // ==================== KIEM TRA & DAM BAO DUONG DAN ====================

    /**
     * Kiem tra mot tap tin co ton tai va la file (khong phai thu muc) hay khong.
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Dam bao thu muc cha cua duong dan da cho ton tai, tu dong tao neu chua co
     * (tao ca cac thu muc trung gian). Dung truoc khi ghi file lan dau, vi du
     * truoc khi CsvStorage ghi ra "data/transactions.csv".
     */
    public static void ensureParentDirectoryExists(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Duong dan tap tin khong duoc de trong!");
        }
        Path parent = Paths.get(filePath).toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Dam bao tap tin ton tai: neu chua co thi tao moi (rong), dong thoi tao
     * luon cac thu muc cha con thieu. Dung khi ung dung khoi dong lan dau va
     * chua co du lieu (VD: data/wallets.csv chua ton tai).
     */
    public static void ensureFileExists(String filePath) throws IOException {
        ensureParentDirectoryExists(filePath);
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    // ==================== DOC FILE ====================

    /**
     * Doc toan bo noi dung tap tin thanh mot chuoi (UTF-8). Dung cho cac tap tin
     * nho nhu file cau hinh hoac file JSON.
     */
    public static String readFileContent(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return "";
        }
        return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /**
     * Doc tap tin thanh danh sach cac dong van ban (UTF-8). Dung pho bien cho
     * CsvStorage khi doc file CSV theo tung dong giao dich/vi/danh muc.
     */
    public static List<String> readLines(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return List.of();
        }
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    // ==================== GHI FILE ====================

    /**
     * Ghi de toan bo noi dung vao tap tin (UTF-8), tu dong tao thu muc cha va
     * tap tin neu chua ton tai.
     */
    public static void writeFileContent(String filePath, String content) throws IOException {
        ensureParentDirectoryExists(filePath);
        Files.writeString(Paths.get(filePath), content == null ? "" : content,
                StandardCharsets.UTF_8);
    }

    /**
     * Ghi de danh sach cac dong van ban vao tap tin (UTF-8), moi phan tu tren
     * mot dong. Dung pho bien cho CsvStorage khi luu lai toan bo danh sach
     * giao dich/vi/danh muc sau khi them/sua/xoa.
     */
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        ensureParentDirectoryExists(filePath);
        Files.write(Paths.get(filePath), lines == null ? List.of() : lines,
                StandardCharsets.UTF_8);
    }

    /**
     * Ghi noi them (append) mot dong van ban vao cuoi tap tin (UTF-8), tu dong
     * tao tap tin neu chua ton tai. Dung khi them nhanh mot giao dich moi ma
     * khong can doc/ghi lai toan bo file.
     */
    public static void appendLine(String filePath, String line) throws IOException {
        ensureFileExists(filePath);
        Files.writeString(Paths.get(filePath), (line == null ? "" : line) + System.lineSeparator(),
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
    }

    // ==================== SAO LUU & XOA ====================

    /**
     * Tao ban sao luu (backup) cua tap tin truoc khi ghi de, ten file backup
     * co dang "tenfile_backup_20260722_153045.csv". Dung de phong ngua mat du
     * lieu khi ghi de file CSV/JSON chinh cua ung dung.
     */
    public static String backupFile(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return null;
        }
        Path source = Paths.get(filePath);
        String fileName = source.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : fileName.substring(dotIndex);
        String timestamp = LocalDateTime.now().format(BACKUP_TIMESTAMP_FORMAT);

        Path backupPath = source.resolveSibling(baseName + BACKUP_SUFFIX + timestamp + extension);
        Files.copy(source, backupPath, StandardCopyOption.REPLACE_EXISTING);
        return backupPath.toString();
    }

    /**
     * Xoa mot tap tin neu ton tai.
     */
    public static boolean deleteFile(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return false;
        }
        return Files.deleteIfExists(Paths.get(filePath));
    }

    // ==================== TIEN ICH KHAC ====================

    /**
     * Lay dung luong tap tin theo don vi byte.
     */
    public static long getFileSize(String filePath) {
        try {
            if (!fileExists(filePath)) {
                return 0L;
            }
            return Files.size(Paths.get(filePath));
        } catch (IOException ex) {
            throw new UncheckedIOException("Khong the doc dung luong tap tin: " + filePath, ex);
        }
    }

    /**
     * Lay phan mo rong (extension) cua tap tin tu ten file, khong bao gom dau
     * cham. Vi du: "transactions.csv" -> "csv". Dung de validate dinh dang
     * file khi nguoi dung chon file import/export.
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "";
        }

        // Lấy tên file từ đường dẫn (đề phòng trường hợp truyền vào đường dẫn có thư mục)
        String fileName = Path.of(filePath).getFileName().toString();

        int lastIndexOfDot = fileName.lastIndexOf('.');

        // Nếu không có dấu chấm, hoặc dấu chấm nằm ở ký tự đầu tiên (file ẩn như .gitignore)
        if (lastIndexOfDot == -1 || lastIndexOfDot == 0) {
            return "";
        }

        return fileName.substring(lastIndexOfDot + 1);
    }
}
