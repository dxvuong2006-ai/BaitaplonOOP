package com.expensemanager.utils;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.Period;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử các lớp tiện ích xử lý Tiền, Ngày và Mật khẩu")
class UtilsTest {

    @Test
    @DisplayName("CurrencyUtils: Parse chuỗi tiền định dạng Việt Nam chính xác")
    void testCurrencyUtilsParse() {
        assertEquals(1500000.0, CurrencyUtils.parseAmount("1.500.000 ₫"));
        assertEquals(250000.0, CurrencyUtils.parseAmount("250,000 VND"));
        assertThrows(EmptyFieldException.class, () -> CurrencyUtils.parseAmount("   "));
    }

    @Test
    @DisplayName("DateUtils: Kiểm tra 2 ngày có nằm cùng trong 1 chu kỳ hay không")
    void testDateUtilsIsInSamePeriod() {
        LocalDate date1 = LocalDate.of(2026, 8, 1);
        LocalDate date2 = LocalDate.of(2026, 8, 20);
        LocalDate date3 = LocalDate.of(2026, 9, 1);

        assertTrue(DateUtils.isInSamePeriod(date1, date2, Period.MONTH));
        assertFalse(DateUtils.isInSamePeriod(date1, date3, Period.MONTH));
    }

    @Test
    @DisplayName("PasswordUtils: Mã hóa và xác thực mật khẩu chính xác")
    void testPasswordUtilsHashing() {
        String rawPassword = "mySecretPassword123";
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hash(rawPassword, salt);

        assertTrue(PasswordUtils.verify(rawPassword, salt, hashedPassword));
        assertFalse(PasswordUtils.verify("wrongPassword", salt, hashedPassword));
    }
}
