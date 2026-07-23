package com.expensemanager.utils;

import com.expensemanager.model.enums.Period;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;

/** Lớp tiện ích xử lý ngày tháng toàn cục, điều hướng thông qua DateFormatStrategy. */
public final class DateUtils {

    /** Chiến lược mặc định (dd/MM/uuuu). Đánh dấu volatile để an toàn khi đổi strategy. */
    private static volatile DateFormatStrategy defaultStrategy = new SlashDateFormatStrategy();

    /** Ngăn khởi tạo lớp tiện ích. */
    private DateUtils() {
        throw new UnsupportedOperationException("Utility class không thể khởi tạo!");
    }


    /** Trả về chiến lược định dạng mặc định. */
    public static DateFormatStrategy getStrategy() {
        return defaultStrategy;
    }

    /** Thiết lập chiến lược định dạng mặc định. */
    public static void setStrategy(DateFormatStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy không được là null!");
        }
        DateUtils.defaultStrategy = strategy;
    }

    // --- Các hàm Format/Parse sử dụng Strategy mặc định ---

    /** Định dạng ngày theo chiến lược mặc định. */
    public static String formatDate(LocalDate date) {
        return defaultStrategy.format(date);
    }

    /** Phân tích chuỗi ngày theo chiến lược mặc định. */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return defaultStrategy.parse(dateStr);
    }

    /** Kiểm tra chuỗi ngày có hợp lệ hay không. */
    public static boolean isValidDate(String dateStr) {
        return defaultStrategy.isValid(dateStr);
    }

    // --- Các hàm Format/Parse tùy biến (không làm đổi strategy mặc định) ---

    /** Định dạng ngày theo chiến lược được chỉ định. */
    public static String formatDate(LocalDate date, DateFormatStrategy customStrategy) {
        return (customStrategy != null) ? customStrategy.format(date) : formatDate(date);
    }

    /** Phân tích chuỗi ngày theo chiến lược được chỉ định. */
    public static LocalDate parseDate(String dateStr, DateFormatStrategy customStrategy)
            throws DateTimeParseException {
        return (customStrategy != null) ? customStrategy.parse(dateStr) : parseDate(dateStr);
    }

    // --- Các hàm nghiệp vụ tính toán thời gian ---

    /** Tính ngày tiếp theo theo chu kỳ. */
    public static LocalDate getNextDate(LocalDate fromDate, Period period) {
        if (fromDate == null || period == null) {
            return fromDate;
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

    /** Kiểm tra hai ngày có thuộc cùng chu kỳ hay không. */
    public static boolean isInSamePeriod(LocalDate date, LocalDate today, Period period) {
        if (date == null || today == null || period == null) {
            return false;
        }
        switch (period) {
            case DAILY:
                return date.isEqual(today);
            case WEEKLY:
                WeekFields weekFields = WeekFields.ISO;
                int dateWeek = date.get(weekFields.weekOfWeekBasedYear());
                int todayWeek = today.get(weekFields.weekOfWeekBasedYear());
                int dateYear = date.get(weekFields.weekBasedYear());
                int todayYear = today.get(weekFields.weekBasedYear());
                return dateWeek == todayWeek && dateYear == todayYear;
            case MONTH:
                return date.getYear() == today.getYear() && date.getMonthValue() == today.getMonthValue();
            case YEARLY:
                return date.getYear() == today.getYear();
            default:
                return false;
        }
    }
}



