package com.expensemanager.utils;

import com.expensemanager.model.enums.Period;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Kiểm tra ngày tháng. */
public class DateUtils {

    /** Ngăn không cho tạo đối tượng từ bên ngoài do các phương thức đều là static. */
    private DateUtils() {}

    public static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /** Chuyển đổi ngày nhập vào dạng LocalDate thành chuỗi(Để hiển thị). */
    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(FORMATTER) : "";
    }

    /** Chuyển đổi ngày nhập vào dạng chuỗi thành dạng LocalDate(Để tính toán). */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new DateTimeParseException("Ngày không được để trống!", dateStr == null ? "" : dateStr, 0);
        }
        dateStr = dateStr.trim();
        return LocalDate.parse(dateStr, FORMATTER);
    }

    /** Kiểm tra chuỗi có phải là ngày hợp lệ không. */
    public static boolean isValidDate(String dateStr) {
        try {
            parseDate(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /** . */
    public static LocalDate getNextDate(LocalDate fromDate, Period period) {
        if (fromDate == null) {
            throw new IllegalArgumentException("Ngày không được để trống");
        }
        if (period == null) {
            throw new IllegalArgumentException("Chu kỳ không được để trống");
        }
        switch (period) {
            case DAILY:
                return fromDate.plusDays(1);
            case WEEKLY:
                return fromDate.plusWeeks(1);
            case MONTH:
                return fromDate.plusMonths(1);
            case YEARLY:
                return fromDate.plusYears(1);
            default:
                throw new IllegalArgumentException("Chu kỳ không hỗ trợ: " + period);
        }
    }
}
