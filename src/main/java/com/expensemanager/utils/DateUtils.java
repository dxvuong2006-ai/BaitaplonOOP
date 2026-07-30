package com.expensemanager.utils;

import com.expensemanager.model.enums.Period;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.model.enums.FieldType;

/** Kiểm tra ngày tháng. */
public class DateUtils {

    /** Ngăn không cho tạo đối tượng từ bên ngoài do các phương thức đều là static. */
    private DateUtils() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        return LocalDate.parse(dateStr, FORMATTER);
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
}
