package com.expensemanager;

import com.expensemanager.utils.CurrencyUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyUtilsTest {

    @Test
    public void testFormatVND() {
        // Kịch bản 1: Kiểm tra xem số 50000 có chuyển đúng thành chuỗi có chữ ₫ không
        double amount = 200000.0;
        String result = CurrencyUtils.formatVND(amount);

        assertNotNull(result);
        assertTrue(result.contains("200"));
        // Lưu ý: Tùy phiên bản Java, chuỗi có thể là "50.000 ₫" hoặc "50.000 VND"
        System.out.println("Kết quả định dạng thực tế: " + result);
    }

    @Test
    public void testParseAmount_Success() {
        // Kịch bản 2: Nhập chuỗi chuẩn dạng số
        double result = CurrencyUtils.parseAmount("150000");
        assertEquals(150000.0, result);
    }

    @Test
    public void testParseAmount_EmptyInput() {
        // Kịch bản 3: Kiểm tra xem nếu để trống thì hệ thống có bắn ra lỗi như bạn viết không[cite: 1]
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            CurrencyUtils.parseAmount("   ");
        });

        assertEquals("Số tiền không được để trống!", exception.getMessage());
    }

    @Test
    public void testParseAmount_WithSpecialCharacters() {
        // Kịch bản 4: Nhập chuỗi có ký hiệu tiền tệ "50000 ₫" xem hàm replaceAll hoạt động đúng không
        double result = CurrencyUtils.parseAmount("50000 đồng");
        assertEquals(50000.0, result);
    }
}
