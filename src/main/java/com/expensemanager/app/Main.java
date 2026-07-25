package com.expensemanager.app;

import com.expensemanager.view.ConsoleView;

/**
 * Điểm khởi đầu (entry point) của toàn bộ ứng dụng.
 * Đây là nơi duy nhất chương trình Java bắt đầu chạy,
 * có nhiệm vụ khởi tạo và gọi tầng View để hiển thị giao diện.
 */
public class Main {
    public static void main(String[] args) {
        new ConsoleView().start();
    }
}