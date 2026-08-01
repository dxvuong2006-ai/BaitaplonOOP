package com.expensemanager.utils;

import com.expensemanager.model.enums.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    // --- Tests cho formatDate ---

    @Test
    void testFormatDate_Success() {
        LocalDate date = LocalDate.of(2026, 6, 7);
        String result = DateUtils.formatDate(date);
        assertEquals("07/06/2026", result);
    }

    @Test
    void testFormatDate_Null() {
        String result = DateUtils.formatDate(null);
        assertEquals("", result);
    }

    // --- Tests cho parseDate ---

    @Test
    void testParseDate_Success() {
        LocalDate result = DateUtils.parseDate("25/12/2026");
        assertEquals(LocalDate.of(2026, 12, 25), result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void testParseDate_BlankOrNull_ThrowsException(String input) {
        assertThrows(DateTimeParseException.class, () -> DateUtils.parseDate(input));
    }

    @Test
    void testParseDate_InvalidFormat_ThrowsException() {
        assertThrows(DateTimeParseException.class, () -> DateUtils.parseDate("2026-12-25"));
    }

    @Test
    void testParseDate_InvalidDate_ThrowsException() {
        assertThrows(DateTimeParseException.class, () -> DateUtils.parseDate("32/13/2026"));
    }

    // --- Tests cho isValidDate ---

    @ParameterizedTest
    @CsvSource({
            "01/01/2026, true",
            "31/12/2026, true",
            "29/02/2024, true", // Năm nhuận
            "32/01/2026, false", // Ngày không tồn tại
            "01/13/2026, false", // Tháng không tồn tại
            "2026/01/01, false", // Sai định dạng
            ", false",           // Null
            "'', false",         // Chuỗi rỗng
            "'   ', false"       // Chỉ chứa khoảng trắng
    })
    void testIsValidDate(String input, boolean expected) {
        assertEquals(expected, DateUtils.isValidDate(input));
    }
}
