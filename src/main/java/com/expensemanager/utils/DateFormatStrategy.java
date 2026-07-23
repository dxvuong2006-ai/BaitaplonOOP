package com.expensemanager.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Lớp trừu tượng định nghĩa chiến lược định dạng và phân tích ngày tháng. */
public abstract class DateFormatStrategy {

    private final String pattern;
    private final DateTimeFormatter formatter;

    /** Khởi tạo chiến lược với mẫu định dạng ngày tháng. */
    protected DateFormatStrategy(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern không được để trống");
        }
        this.pattern = pattern;
        this.formatter = DateTimeFormatter.ofPattern(pattern)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Trả về mẫu định dạng ngày tháng hiện tại. */
    public final String getPattern() {
        return pattern;
    }

    /** Trả về đối tượng DateTimeFormatter đang sử dụng. */
    protected final DateTimeFormatter getFormatter() {
        return formatter;
    }

    /** Định dạng đối tượng LocalDate thành chuỗi. */
    public String format(LocalDate date) {
        return (date != null) ? date.format(formatter) : "";
    }

    /** Phân tích chuỗi ngày tháng thành đối tượng LocalDate. */
    public LocalDate parse(String dateStr) throws DateTimeParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new DateTimeParseException("Ngày tháng không được để trống", "", 0);
        }
        return LocalDate.parse(dateStr.trim(), formatter);
    }

    /** Kiểm tra chuỗi ngày tháng có đúng định dạng hay không. */
    public final boolean isValid(String dateStr) {
        try {
            parse(dateStr);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
