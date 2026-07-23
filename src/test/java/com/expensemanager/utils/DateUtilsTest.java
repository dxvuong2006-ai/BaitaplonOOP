package com.expensemanager.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lớp kiểm thử cho DateUtils.
 * Tập trung vào việc thay đổi Strategy và các logic tính toán khoảng thời gian.
 */
class DateUtilsTest {

    /** * Đảm bảo sau mỗi test case, Strategy mặc định được reset lại,
     * tránh việc test case này làm hỏng test case khác (Test Isolation).
     */
    @AfterEach
    void tearDown() {
        DateUtils.setStrategy(new SlashDateFormatStrategy());
    }

    @Test
    @DisplayName("DateUtils: Uỷ quyền đúng cho Strategy mặc định (Slash)")
    void testDefaultStrategyDelegation() {
        LocalDate date = LocalDate.of(2026, 1, 15);
        assertEquals("15/01/2026", DateUtils.formatDate(date));
        assertTrue(DateUtils.getStrategy() instanceof SlashDateFormatStrategy);
    }

    @Test
    @DisplayName("DateUtils: Có thể thay đổi Strategy (Đa hình) ở Runtime")
    void testSetStrategy() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        // Đổi sang ISO
        DateUtils.setStrategy(new IsoDateFormatStrategy());
        assertEquals("2026-01-15", DateUtils.formatDate(date));
        assertTrue(DateUtils.getStrategy() instanceof IsoDateFormatStrategy);
    }

    @Test
    @DisplayName("DateUtils: Bắn ngoại lệ IllegalArgumentException nếu set Strategy null")
    void testSetNullStrategy() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DateUtils.setStrategy(null)
        );
        assertEquals("Strategy không được là null!", exception.getMessage());
    }

    @Test
    @DisplayName("DateUtils: Tính ngày đến hạn kế tiếp (nextDueDate) chính xác")
    void testGetNextDate() {
        LocalDate baseDate = LocalDate.of(2026, 1, 15);

        assertEquals(LocalDate.of(2026, 1, 16), DateUtils.getNextDate(baseDate, Period.DAILY));
        assertEquals(LocalDate.of(2026, 1, 22), DateUtils.getNextDate(baseDate, Period.WEEKLY));
        assertEquals(LocalDate.of(2026, 2, 15), DateUtils.getNextDate(baseDate, Period.MONTH));
        assertEquals(LocalDate.of(2027, 1, 15), DateUtils.getNextDate(baseDate, Period.YEARLY));
    }

    @Test
    @DisplayName("DateUtils: Kiểm tra cùng khoảng thời gian (isInSamePeriod)")
    void testIsInSamePeriod() {
        LocalDate today = LocalDate.of(2026, 7, 22);

        // DAILY
        assertTrue(DateUtils.isInSamePeriod(LocalDate.of(2026, 7, 22), today, Period.DAILY));
        assertFalse(DateUtils.isInSamePeriod(LocalDate.of(2026, 7, 23), today, Period.DAILY));

        // MONTHLY
        assertTrue(DateUtils.isInSamePeriod(LocalDate.of(2026, 7, 1), today, Period.MONTH));
        assertFalse(DateUtils.isInSamePeriod(LocalDate.of(2026, 8, 22), today, Period.MONTH));

        // YEARLY
        assertTrue(DateUtils.isInSamePeriod(LocalDate.of(2026, 1, 1), today, Period.YEARLY));
        assertFalse(DateUtils.isInSamePeriod(LocalDate.of(2027, 7, 22), today, Period.YEARLY));
    }

    @Test
    @DisplayName("DateUtils: Xử lý giao năm cho tuần chuẩn xác (WEEKLY Edge Case)")
    void testIsInSamePeriodWeeklyEdgeCase() {
        // 30/12/2024 và 31/12/2024 (Đều thuộc Tuần 1 của năm 2025 theo chuẩn ISO)
        LocalDate dec30 = LocalDate.of(2024, 12, 30);
        LocalDate dec31 = LocalDate.of(2024, 12, 31);
        LocalDate jan1 = LocalDate.of(2025, 1, 1);

        assertTrue(DateUtils.isInSamePeriod(dec30, dec31, Period.WEEKLY));
        assertTrue(DateUtils.isInSamePeriod(dec31, jan1, Period.WEEKLY));
    }
}
