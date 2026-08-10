package com.expensemanager.controller;

import com.expensemanager.model.user.User;
import com.expensemanager.service.ExpenseManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controller điều khiển khung sườn chính của ứng dụng (main.fxml).
 * Chịu trách nhiệm điều hướng màn hình và hiển thị
 * thông tin tài khoản đang đăng nhập trên sidebar.
 */
public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label sidebarUsernameLabel;

    @FXML
    private Label sidebarAvatarLabel;

    private final ExpenseManager expenseManager = ExpenseManager.getInstance();

    @FXML
    private void initialize() {
        loadCurrentUser();
        loadView("dashboard.fxml");
    }

    /**
     * Hiển thị tài khoản đang đăng nhập trên sidebar.
     */
    private void loadCurrentUser() {
        User currentUser = expenseManager.getCurrentUser();

        if (currentUser == null) {
            sidebarUsernameLabel.setText("Khách");
            sidebarAvatarLabel.setText("?");
            return;
        }

        String username = currentUser.getUsername();

        if (username == null || username.isBlank()) {
            sidebarUsernameLabel.setText("Người dùng");
            sidebarAvatarLabel.setText("U");
            return;
        }

        sidebarUsernameLabel.setText(username);

        String firstLetter =
                username.substring(0, 1).toUpperCase();

        sidebarAvatarLabel.setText(firstLetter);
    }

    /**
     * Hàm dùng chung để nạp một file FXML
     * vào vùng nội dung chính.
     */
    private void loadView(String fxmlFile) {
        try {
            Parent view =
                    FXMLLoader.load(
                            getClass().getResource(
                                    "/com/expensemanager/view/" + fxmlFile));

            contentPane.getChildren().setAll(view);

        } catch (IOException e) {
            System.out.println(
                    "Không thể tải giao diện: " + fxmlFile);
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