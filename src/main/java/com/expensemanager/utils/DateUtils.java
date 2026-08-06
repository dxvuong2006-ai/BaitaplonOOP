package com.expensemanager.utils;

import com.expensemanager.model.enums.Period;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.model.enums.FieldType;

import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.IsoFields;

/** Kiểm tra ngày tháng. */
public class DateUtils {

    /** Ngăn không cho tạo đối tượng từ bên ngoài do các phương thức đều là static. */
    private DateUtils() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Chuyển đổi ngày nhập vào dạng LocalDate thành chuỗi(Để hiển thị). */
    public static String formatDate(LocalDate date) {
        return (date != null) ? date.format(FORMATTER) : "";
    }

    /** Chuyển đổi ngày nhập vào dạng chuỗi thành dạng LocalDate(Để tính toán). */
    public static LocalDate parseDate(String dateStr)  {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        dateStr = dateStr.trim();
        try {
            dateStr = dateStr.trim();
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidFormatException(FieldType.DATE, "Ngày không hợp lệ");
        }
    }

    /** Kiểm tra chuỗi có phải là ngày hợp lệ không. */
    public static boolean isValidDate(String dateStr) {
        try {
            parseDate(dateStr);
            return true;
        } catch (InvalidFormatException e) {
            return false;
        }
    }

    /** Kiểm tra xem 2 ngày có cùng trong 1 chu kỳ hay không. */
    public static boolean isInSamePeriod(LocalDate date,
                                         LocalDate referenceDate,
                                         Period period) {
        if (date == null || referenceDate == null || period == null) {
            return false;
        }
        switch (period) {
            case DAILY:
                return date.equals(referenceDate);
            case WEEKLY:
                return date.getYear() == referenceDate.getYear()
                        && date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                        == referenceDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            case MONTH:
                return date.getYear() == referenceDate.getYear()
                        && date.getMonth() == referenceDate.getMonth();
            case YEARLY:
                return date.getYear() == referenceDate.getYear();
            default:
                return false;
        }
    }
}
