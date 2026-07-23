package com.expensemanager;

import com.expensemanager.utils.CurrencyUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Lớp Tiện ích CurrencyUtils")
class CurrencyUtilsTest {

    @Test
    @DisplayName("Kiểm tra private constructor chống tạo đối tượng")
    void testPrivateConstructor() throws NoSuchMethodException {
        Constructor<CurrencyUtils> constructor = CurrencyUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    }

    @Nested
    @DisplayName("Kiểm thử Định dạng (Formatting)")
    class FormattingTests {

        @Test
        @DisplayName("Định dạng chuẩn VND")
        void testFormatVND() {
            String result = CurrencyUtils.formatVND(50000);
            assertEquals("50.000 ₫", result);
        }

        @Test
        @DisplayName("Định dạng số có dấu (+/-)")
        void testFormatSignedVND() {
            assertEquals("+50.000 ₫", CurrencyUtils.formatSignedVND(50000));
            assertEquals("-20.000 ₫", CurrencyUtils.formatSignedVND(-20000));
        }

        @Test
        @DisplayName("Định dạng phân cách hàng nghìn")
        void testFormatNumber() {
            assertEquals("1.234.567", CurrencyUtils.formatNumber(1234567));
        }
    }

    @Nested
    @DisplayName("Kiểm thử Ép kiểu chuỗi (Parsing)")
    class ParsingTests {

        @ParameterizedTest
        @CsvSource({
                "'50000', 50000.0",
                "'50.000', 50000.0",
                "'50.000 ₫', 50000.0",
                "'1.000.000 VND', 1000000.0",
                "'50,000.50', 50000.50",
                "'50.000,50', 50000.50",
                "'-20.000 ₫', -20000.0"
        })
        @DisplayName("Chuyển chuỗi hợp lệ sang double")
        void testParseAmountValid(String input, double expected) {
            assertEquals(expected, CurrencyUtils.parseAmount(input), 0.0001);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "abc", "VND", "₫"})
        @DisplayName("Ném lỗi với chuỗi không hợp lệ")
        void testParseAmountInvalid(String input) {
            assertThrows(NumberFormatException.class, () -> CurrencyUtils.parseAmount(input));
        }
    }

    @Nested
    @DisplayName("Kiểm thử Các hàm hỗ trợ")
    class LogicTests {

        @Test
        @DisplayName("Kiểm tra isValidAmount và isPositiveAmount")
        void testValidation() {
            assertTrue(CurrencyUtils.isValidAmount("50.000 ₫"));
            assertFalse(CurrencyUtils.isValidAmount("invalid"));

            assertTrue(CurrencyUtils.isPositiveAmount(100.0));
            assertFalse(CurrencyUtils.isPositiveAmount(0.0));
            assertFalse(CurrencyUtils.isPositiveAmount(-50.0));
        }

        @Test
        @DisplayName("So sánh bằng isEqual và làm tròn roundToDong")
        void testMathHelpers() {
            assertTrue(CurrencyUtils.isEqual(100.00001, 100.00002));
            assertEquals(50000.0, CurrencyUtils.roundToDong(50000.4));
            assertEquals(50001.0, CurrencyUtils.roundToDong(50000.6));
        }
    }
}
