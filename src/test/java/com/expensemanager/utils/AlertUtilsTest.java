package com.expensemanager.utils;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử AlertUtils")
class AlertUtilsTest {

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Khởi tạo môi trường JavaFX Toolkit để tránh lỗi "Toolkit not initialized"
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            // Môi trường JavaFX đã được khởi tạo trước đó
            latch.countDown();
        }
        // Chờ JavaFX Toolkit sẵn sàng
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Không thể khởi tạo JavaFX Toolkit");
    }

    @Test
    @DisplayName("Kiểm tra Constructor riêng tư không thể khởi tạo trực tiếp")
    void testPrivateConstructor() throws Exception {
        Constructor<AlertUtils> constructor = AlertUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance,
                "Nên ném ngoại lệ khi gọi constructor của utility class"
        );

        assertInstanceOf(
                UnsupportedOperationException.class,
                exception.getCause(),
                "Nguyên nhân ném lỗi phải là UnsupportedOperationException"
        );
        assertEquals("Utility class khong the khoi tao!", exception.getCause().getMessage());
    }

    @Nested
    @DisplayName("Kiểm thử xử lý Ngoại lệ trong showError")
    class ExceptionFormattingTests {

        @Test
        @DisplayName("Xử lý chính xác khi Throwable là null")
        void testShowErrorWithNullThrowable() {
            // Kiểm tra việc trích xuất thông điệp khi ex == null mà không gây ra lỗi UI
            assertDoesNotThrow(() -> {
                Platform.runLater(() -> AlertUtils.showError("Tiêu đề", "Bối cảnh", (Throwable) null));
            });
        }

        @Test
        @DisplayName("Xử lý chính xác khi Throwable có getMessage() là null")
        void testShowErrorWithNullMessageThrowable() {
            Throwable exWithoutMessage = new NullPointerException();
            assertDoesNotThrow(() -> {
                Platform.runLater(() -> AlertUtils.showError("Tiêu đề", "Bối cảnh", exWithoutMessage));
            });
        }

        @Test
        @DisplayName("Xử lý chính xác khi Throwable có thông điệp cụ thể")
        void testShowErrorWithValidThrowable() {
            Throwable exWithMessage = new IllegalArgumentException("Số dư không đủ");
            assertDoesNotThrow(() -> {
                Platform.runLater(() -> AlertUtils.showError("Tiêu đề", "Bối cảnh", exWithMessage));
            });
        }
    }

    @Nested
    @DisplayName("Kiểm thử các phương thức chạy bất đồng bộ (*Later)")
    class AsyncMethodsTests {

        @Test
        @DisplayName("showInfoLater không gây ra ngoại lệ trên Thread khác")
        void testShowInfoLater() {
            assertDoesNotThrow(() -> AlertUtils.showInfoLater("Tiêu đề", "Nội dung"));
        }

        @Test
        @DisplayName("showErrorLater không gây ra ngoại lệ trên Thread khác")
        void testShowErrorLater() {
            assertDoesNotThrow(() -> AlertUtils.showErrorLater("Tiêu đề", "Nội dung"));
            assertDoesNotThrow(() -> AlertUtils.showErrorLater("Tiêu đề", "Bối cảnh", new RuntimeException("Lỗi")));
        }
    }
}
