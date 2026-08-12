package com.expensemanager.controller;

import com.expensemanager.model.user.User;
import com.expensemanager.service.ExpenseManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void handleRegister() {

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username == null || username.isBlank()) {
            showError("Vui lòng nhập tên đăng nhập.");
            usernameField.requestFocus();
            return;
        }

        username = username.trim();

        if (username.length() < 3) {
            showError("Tên đăng nhập phải có ít nhất 3 ký tự.");
            usernameField.requestFocus();
            return;
        }

        if (password == null || password.isBlank()) {
            showError("Vui lòng nhập mật khẩu.");
            passwordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự.");
            passwordField.requestFocus();
            return;
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            showError("Vui lòng nhập lại mật khẩu.");
            confirmPasswordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp.");
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }

        email = email == null ? "" : email.trim();

        if (expenseManager.findByUsername(username) != null) {
            showError("Tên đăng nhập đã tồn tại.");
            usernameField.requestFocus();
            return;
        }

        try {

            String id = UUID.randomUUID().toString();

            User user =
                    expenseManager.register(
                            id,
                            username,
                            password,
                            email
                    );

            if (user == null) {
                showError("Không thể tạo tài khoản.");
                return;
            }

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Đăng ký thành công");
            alert.setHeaderText(null);

            alert.setContentText(
                    "Tạo tài khoản thành công.\n"
                            + "Vui lòng đăng nhập để tiếp tục."
            );

            alert.showAndWait();

            openLoginWindow();

        } catch (Exception e) {

            String message = e.getMessage();

            showError(
                    message == null || message.isBlank()
                            ? "Đăng ký tài khoản thất bại."
                            : message
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {

        try {

            openLoginWindow();

        } catch (IOException e) {

            showError(
                    "Không thể mở màn hình đăng nhập."
            );

            e.printStackTrace();
        }
    }

    private void openLoginWindow() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/com/expensemanager/view/login.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage =
                (Stage) usernameField
                        .getScene()
                        .getWindow();

        Scene scene = new Scene(root);

        // Nền cùng màu với card login để không lộ viền trắng
        scene.setFill(Color.web("#1b2639"));

        stage.setScene(scene);

        stage.setTitle(
                "Đăng nhập - Quản Lý Chi Tiêu Cá Nhân"
        );

        stage.setResizable(false);

        // Co cửa sổ đúng theo kích thước login.fxml
        stage.sizeToScene();

        // Căn lại giữa màn hình
        stage.centerOnScreen();

        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }
}
