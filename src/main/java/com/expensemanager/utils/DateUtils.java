package com.expensemanager.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Lop DateUtils xử lý ngày tháng. */
public final class DateUtils {

    /**
     * Ngăn không cho tạo đối tượng từ bên ngoài.
     * Khóa Constructor để không cần tạo đối tượng bên ngoài nữa.
     */
    private DateUtils() {}


    public static final String PATTERN = "dd/MM/uuuu";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern(PATTERN)
            .withResolverStyle(ResolverStyle.STRICT);

    /** Định dạng ngày tháng thành dạng chuỗi dd/MM/uuuu. */
    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(FORMATTER) : "";
    }

    /**
     * Chuyển đổi chuỗi dd/MM/uuuu thành dạng LocalDate giúp tính toán hoặc so sánh.
     * Nếu ngày tháng sai thì sẽ bị ném ra ngoại lệ để xử lý.
     */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        if (dateStr == null) {
            throw new DateTimeParseException("Ngày tháng năm không được để trống", "", 0);
        }
        return LocalDate.parse(dateStr, FORMATTER);
    }

    /** Kiểm tra giá trị ngày tháng nhập vào có hợp lệ không. */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null) {
            return false;
        }
        try {
            parseDate(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
