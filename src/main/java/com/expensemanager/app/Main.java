package com.expensemanager.app;

import com.expensemanager.view.ConsoleView;

/**
 * Điểm khởi đầu của toàn bộ ứng dụng.
 * Có thể chuyển giữa chế độ Console và giao diện JavaFX.
 */
public class Main {

    public static void main(String[] args) {

        // Chế độ dòng lệnh:
        // new ConsoleView().start();

        // Chế độ giao diện JavaFX:
        AppLauncher.main(args);
    }
}