package com.expensemanager;

import com.expensemanager.utils.DateUtils;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

/** Lop kiểm tra lop DateUtils. */
public class DateUtilsTest {

    /** 1. Kiểm thử phương thức formatDate(). */
    @Test
    public void testFormatDate_WithValidDate() {
        // Chuẩn bị dữ liệu: Tạo một ngày 25/12/2026
        LocalDate date = LocalDate.of(2026, 12, 25);

        // Thực thi
        String result = DateUtils.formatDate(date);

        // Kiểm tra kết quả có đúng định dạng dd/MM/yyyy không
        assertEquals("25/12/2026", result, "Định dạng ngày tháng bị sai!");
    }

    /** Kiểm tra với giá trị ngày tháng rỗng. */
    @Test
    public void testFormatDate_WithNullDate() {
        // Thực thi với giá trị null
        String result = DateUtils.formatDate(null);

        // Kiểm tra kết quả phải trả về chuỗi rỗng để không bị crash app
        assertEquals("", result, "Nếu date là null thì phải trả về chuỗi rỗng!");
    }

    /** 2. Kiểm thử phương thức parseDate(). */
    @Test
    public void testParseDate_WithValidString() {
        // Thực thi
        LocalDate result = DateUtils.parseDate("01/01/2025");

        // Kiểm tra
        assertNotNull(result);
        assertEquals(2025, result.getYear());
        assertEquals(01, result.getMonthValue());
        assertEquals(01, result.getDayOfMonth());
    }

    /** Kiểm tra với ngày tháng bị lỗi phông. */
    @Test
    public void testParseDate_WithInvalidFormat() {
        // Kiểm tra xem phương thức có NÉM RA đúng lỗi DateTimeParseException khi nhập sai form không
        assertThrows(DateTimeParseException.class, () -> {
            DateUtils.parseDate("25-12-2026"); // Sai dấu phân cách
        }, "Phải ném ra ngoại lệ khi chuỗi sai định dạng!");
    }

    /** Kiểm tra với giá trị ngày tháng không tồn tại. */
    @Test
    public void testParseDate_WithNonExistentDate() {
        // Kiểm tra với ngày không có thực (Tháng 2 năm không nhuận làm gì có ngày 29)
        assertThrows(DateTimeParseException.class, () -> {
            DateUtils.parseDate("29/02/2025");
        }, "Phải ném ra ngoại lệ với những ngày không tồn tại trên lịch!");
    }

    /** 3. Kiểm thử phương thức isValidDate(). */
    @Test
    public void testIsValidDate_WithValidString() {
        // Ngày chuẩn, năm nhuận có 29/2
        assertTrue(DateUtils.isValidDate("29/02/2024"), "Ngày 29/02/2024 là hợp lệ!");
        assertTrue(DateUtils.isValidDate("15/08/2026"), "Ngày 15/08/2026 là hợp lệ!");
    }

    /** Kiểm tra với giá trị ngày không tồn tại. */
    @Test
    public void testIsValidDate_WithInvalidString() {
        // Các trường hợp sai định dạng hoặc ngày hư cấu
        assertFalse(DateUtils.isValidDate("31/04/2026"), "Tháng 4 chỉ có 30 ngày, phải trả về false!");
        assertFalse(DateUtils.isValidDate("abc"), "Chuỗi chữ cái phải trả về false!");
        assertFalse(DateUtils.isValidDate("1/1/2026"), "Thiếu số 0 ở ngày và tháng, phải trả về false!");
    }

    /** Kiểm tra với giá trị ngày tháng rỗng. */
    @Test
    public void testIsValidDate_WithNull() {
        // Xử lý an toàn khi truyền null
        assertFalse(DateUtils.isValidDate(null), "Truyền null vào thì phải trả về false, không được crash!");
    }
}
