package com.expensemanager.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AlertUtilsTest {

    @Test
    void testShowInfo_DoesNotThrowException() {
        assertDoesNotThrow(() -> {
            AlertUtils.showInfo("Thông báo", "Đây là nội dung thông tin.");
        });
    }

    @Test
    void testShowError_DoesNotThrowException() {
        assertDoesNotThrow(() -> {
            AlertUtils.showError("Lỗi", "Đã có lỗi xảy ra.");
        });
    }

    @Test
    void testShowWarning_DoesNotThrowException() {
        assertDoesNotThrow(() -> {
            AlertUtils.showWarning("Cảnh báo", "Hãy cẩn thận.");
        });
    }

    @Test
    void testShowMethods_WithNullParameters_DoesNotThrowException() {
        assertDoesNotThrow(() -> {
            AlertUtils.showInfo(null, null);
            AlertUtils.showError(null, null);
            AlertUtils.showWarning(null, null);
        });
    }
}
