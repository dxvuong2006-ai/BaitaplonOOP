package com.expensemanager.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lớp kiểm thử tự động (Unit Test) cho CurrencyUtils sử dụng JUnit 5.
 */
class CurrencyUtilsTest {

    @Test
    @DisplayName("Test định dạng số tiền thành chuỗi VND")
    void testFormatVND() {
        String formatted = CurrencyUtils.formatVND(50000.0);
        assertNotNull(formatted);
        // Kiểm tra xem chuỗi định dạng có chứa số hoặc ký hiệu tiền tệ không
        assertTrue(formatted.contains("50.000") || formatted.contains("50000"));
    }

    @ParameterizedTest
    @DisplayName("Test bóc tách chuỗi tiền tệ thành kiểu double (parseAmount)")
    @CsvSource({
            "50.000 ₫, 50000.0",
            "100.000VND, 100000.0",
            "1.234.567 vnd, 1234567.0",
            "50000, 50000.0"
    })
    void testParseAmountSuccess(String input, double expected) {
        double result = CurrencyUtils.parseAmount(input);
        assertEquals(expected, result, 0.0001);
    }

    @ParameterizedTest
    @DisplayName("Test parseAmount với chuỗi không hợp lệ hoặc rỗng ném ngoại lệ")
    @ValueSource(strings = {"", "   ", "abc"})
    void testParseAmountException(String input) {
        assertThrows(NumberFormatException.class, () -> {
            CurrencyUtils.parseAmount(input);
        });
    }

    @Test
    @DisplayName("Test parseAmount với giá trị null ném ngoại lệ")
    void testParseAmountNull() {
        assertThrows(NumberFormatException.class, () -> {
            CurrencyUtils.parseAmount(null);
        });
    }

    @ParameterizedTest
    @DisplayName("Test kiểm tra tính hợp lệ của chuỗi số tiền (isValidAmount)")
    @CsvSource({
            "50.000 ₫, true",
            "100.000VND, true",
            "abc, false",
            ", false",
            "'   ', false"
    })
    void testIsValidAmount(String input, boolean expected) {
        assertEquals(expected, CurrencyUtils.isValidAmount(input));
    }

    @ParameterizedTest
    @DisplayName("Test kiểm tra số tiền dương (isPositiveAmount)")
    @CsvSource({
            "100.0, true",
            "0.1, true",
            "0.0, false",
            "-50.0, false"
    })
    void testIsPositiveAmount(double amount, boolean expected) {
        assertEquals(expected, CurrencyUtils.isPositiveAmount(amount));
    }

    @Test
    @DisplayName("Test so sánh hai giá trị số thực với sai số EPSILON (isEqual)")
    void testIsEqual() {
        assertTrue(CurrencyUtils.isEqual(10.00002, 10.00001)); // Sai số nhỏ hơn EPSILON (0.0001)
        assertFalse(CurrencyUtils.isEqual(10.001, 10.0));       // Sai số lớn hơn EPSILON
    }

    @Test
    @DisplayName("Test private constructor để đạt độ bao phủ tuyệt đối qua Reflection")
    void testPrivateConstructor() throws Exception {
        Constructor<CurrencyUtils> constructor = CurrencyUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Vì constructor không ném ngoại lệ tường minh nhưng có thể được gọi hoặc bọc trong InvocationTargetException
        assertDoesNotThrow(() -> {
            constructor.newInstance();
        });
    }
}
