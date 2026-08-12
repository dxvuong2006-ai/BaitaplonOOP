package com.expensemanager.controller;

import com.expensemanager.exception.ExpenseManagerException;
import com.expensemanager.model.user.User;
import com.expensemanager.service.ExpenseManager;

import java.io.IOException;

import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller điều khiển khung sườn chính của ứng dụng.
 * Quản lý điều hướng, thông tin người dùng và đăng xuất.
 */
public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label sidebarUsernameLabel;

    @FXML
    private Label sidebarAvatarLabel;

    @FXML
    private VBox userMenuBox;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    @FXML
    private void initialize() {
        loadCurrentUser();
        hideUserMenu();
        //expenseManager.processDueExpenses();
        loadView("dashboard.fxml");
        Platform.runLater(() -> {
            if (contentPane != null && contentPane.getScene() != null) {
                Stage stage = (Stage) contentPane.getScene().getWindow();
                if (stage != null) {
                    stage.setResizable(true); // Cho phép phóng to full màn hình
                }
            }
        });
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

        sidebarAvatarLabel.setText(
                username.substring(0, 1).toUpperCase()
        );
    }

    /**
     * Bấm card tài khoản để hiện / ẩn menu.
     */
    @FXML
    private void handleToggleUserMenu() {

        boolean show = !userMenuBox.isVisible();

        userMenuBox.setVisible(show);
        userMenuBox.setManaged(show);
    }

    /**
     * Ẩn menu tài khoản.
     */
    private void hideUserMenu() {
        userMenuBox.setVisible(false);
        userMenuBox.setManaged(false);
    }

    /**
     * Đăng xuất và quay lại màn hình đăng nhập.
     */
    @FXML
    private void handleLogout() {
        try {
            expenseManager.logout();
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/com/expensemanager/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentPane.getScene().getWindow();
            Scene scene = new Scene(root);

            scene.setFill(Color.web("#1b2639"));
            stage.setMaximized(false);

            stage.setScene(scene);
            stage.setTitle("Đăng nhập - Quản Lý Chi Tiêu Cá Nhân");
            stage.setResizable(false);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {

            System.out.println(
                    "Không thể tải giao diện đăng nhập."
            );

            e.printStackTrace();
        }
    }

    /**
     * Load màn hình con vào vùng nội dung chính.
     */
    private void loadView(String fxmlFile) {

        try {

            Parent view =
                    FXMLLoader.load(
                            getClass().getResource(
                                    "/com/expensemanager/view/" + fxmlFile
                            )
                    );

            contentPane.getChildren().setAll(view);

        } catch (IOException e) {

            System.out.println(
                    "Không thể tải giao diện: " + fxmlFile
            );

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
