package com.expensemanager.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lớp kiểm thử cho các chiến lược định dạng ngày (Strategy Pattern).
 * Đảm bảo các lớp con xử lý chuỗi và định dạng chính xác.
 */
class DateFormatStrategyTest {

    private final DateFormatStrategy slashStrategy = new SlashDateFormatStrategy();
    private final DateFormatStrategy isoStrategy = new IsoDateFormatStrategy();

    @Test
    @DisplayName("SlashStrategy: Định dạng và phân tích đúng chuẩn dd/MM/uuuu")
    void testSlashStrategyValid() {
        LocalDate date = LocalDate.of(2026, 7, 22);

        // Test Format
        assertEquals("22/07/2026", slashStrategy.format(date));

        // Test Parse
        assertEquals(date, slashStrategy.parse("22/07/2026"));

        // Test isValid
        assertTrue(slashStrategy.isValid("22/07/2026"));
        assertFalse(slashStrategy.isValid("2026-07-22")); // Sai pattern
    }

    @Test
    @DisplayName("IsoStrategy: Định dạng và phân tích đúng chuẩn uuuu-MM-dd")
    void testIsoStrategyValid() {
        LocalDate date = LocalDate.of(2026, 7, 22);

        // Test Format
        assertEquals("2026-07-22", isoStrategy.format(date));

        // Test Parse
        assertEquals(date, isoStrategy.parse("2026-07-22"));

        // Test isValid
        assertTrue(isoStrategy.isValid("2026-07-22"));
        assertFalse(isoStrategy.isValid("22/07/2026")); // Sai pattern
    }

    @Test
    @DisplayName("DateFormatStrategy: Bắn ngoại lệ DateTimeParseException khi chuỗi sai")
    void testStrategyParseExceptions() {
        // Chuỗi rỗng hoặc null
        assertThrows(DateTimeParseException.class, () -> slashStrategy.parse(""));
        assertThrows(DateTimeParseException.class, () -> slashStrategy.parse(null));
        assertThrows(DateTimeParseException.class, () -> slashStrategy.parse("   "));

        // Chuỗi chứa ký tự chữ
        assertThrows(DateTimeParseException.class, () -> slashStrategy.parse("22/AA/2026"));

        // Ngày không tồn tại (31/02)
        assertThrows(DateTimeParseException.class, () -> slashStrategy.parse("31/02/2026"));
    }

    @Test
    @DisplayName("DateFormatStrategy: Xử lý an toàn khi format giá trị null")
    void testStrategyFormatNull() {
        assertEquals("", slashStrategy.format(null));
        assertEquals("", isoStrategy.format(null));
    }
}
