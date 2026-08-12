package com.expensemanager.controller;

import com.expensemanager.model.user.User;
import com.expensemanager.service.ExpenseManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void handlePasswordKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank()) {
            errorLabel.setText("Vui lòng nhập tên đăng nhập.");
            usernameField.requestFocus();
            return;
        }

        if (password == null || password.isBlank()) {
            errorLabel.setText("Vui lòng nhập mật khẩu.");
            passwordField.requestFocus();
            return;
        }

        try {

            User user =
                    expenseManager.login(
                            username.trim(),
                            password
                    );

            if (user == null) {
                errorLabel.setText(
                        "Tên đăng nhập hoặc mật khẩu không đúng."
                );

                passwordField.clear();
                passwordField.requestFocus();
                return;
            }

            openMainWindow();

        } catch (Exception e) {

            errorLabel.setText(
                    "Không thể đăng nhập: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void openMainWindow() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/com/expensemanager/view/main.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage =
                (Stage) usernameField
                        .getScene()
                        .getWindow();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Quản Lý Chi Tiêu Cá Nhân");
        stage.setResizable(true); // Cho phép phóng to / thu nhỏ giao diện chính
        stage.centerOnScreen();   // Căn giữa màn hình sau khi đổi kích thước
        stage.show();
    }

    @FXML
    private void handleOpenRegister() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/com/expensemanager/view/register.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage =
                (Stage) usernameField
                        .getScene()
                        .getWindow();

        Scene scene = new Scene(root);

        scene.setFill(Color.web("#1b2639"));

        stage.setScene(scene);
        stage.setTitle("Đăng ký - Quản Lý Chi Tiêu Cá Nhân");
        stage.setResizable(false);

        // Cho cửa sổ ôm đúng kích thước register.fxml
        stage.sizeToScene();

        // Căn lại cửa sổ vào giữa màn hình
        stage.centerOnScreen();

        stage.show();
    }
}
