package com.expensemanager.app;

import com.expensemanager.view.ConsoleView;

/**
 * Điểm khởi đầu (entry point) của toàn bộ ứng dụng.
 * Tuỳ chỉnh dòng gọi bên dưới để chạy phiên bản Console hoặc GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Chế độ dòng lệnh (Tuần 1-2)
        // new ConsoleView().start();

        // Chế độ giao diện đồ hoạ (Tuần 3)
        AppLauncher.main(args);
    }
}