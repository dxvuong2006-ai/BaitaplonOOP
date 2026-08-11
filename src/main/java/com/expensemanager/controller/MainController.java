package com.expensemanager.controller;

import com.expensemanager.exception.ExpenseManagerException;
import com.expensemanager.service.ExpenseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controller điều khiển khung sườn chính của ứng dụng (main.fxml).
 * Chịu trách nhiệm duy nhất: nhận sự kiện bấm nút trên thanh điều hướng,
 * rồi nạp (load) đúng màn hình con tương ứng vào vùng nội dung trung tâm.
 */
public class MainController {

    // @FXML cho phép JavaFX tự động "tiêm" (inject) đối tượng StackPane
    // có fx:id="contentPane" khai báo trong main.fxml vào đúng biến này.
    @FXML
    private StackPane contentPane;

    private final ExpenseManager manager = ExpenseManager.getInstance();

    @FXML
    private void initialize() {

        processDueRecurringExpenses();

        loadView("dashboard.fxml");
    }

    /**
     * Xử lý các khoản chi tiêu định kỳ đã đến hạn của người dùng hiện tại.
     * Lỗi ở bước này (nếu có) chỉ được log ra console, KHÔNG được để văng
     * lên UI hay chặn việc mở dashboard — vì đây là một tác vụ nền tự động,
     * người dùng không chủ động bấm nút để chạy nó, nên không nên bị chặn
     * truy cập ứng dụng chỉ vì một lỗi ở tác vụ nền.
     */
    private void processDueRecurringExpenses() {
        try {
            manager.processDueExpenses();
        } catch (ExpenseManagerException e) {
            System.err.println("Không thể xử lý khoản chi định kỳ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hàm dùng chung để nạp 1 file FXML bất kỳ vào vùng nội dung chính.
     * Tách riêng thành 1 hàm để tránh lặp code ở mỗi handleShowXXX().
     */
    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/com/expensemanager/view/" + fxmlFile));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.out.println("Không thể tải giao diện: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowDashboard() {
        loadView("dashboard.fxml");
    }

    @FXML
    private void handleShowTransaction() {
        loadView("transaction.fxml");
    }

    @FXML
    private void handleShowWallet() {
        loadView("wallet.fxml");
    }

    @FXML
    private void handleShowCategory() {
        loadView("category.fxml");
    }

    @FXML
    private void handleShowBudget() {
        loadView("budget.fxml");
    }

    @FXML
    private void handleShowReport() {
        loadView("report.fxml");
    }
}
