package com.expensemanager.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/com/expensemanager/view/login.fxml"
                        )
                );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Nền Scene cùng màu với card để không lộ viền trắng
        scene.setFill(Color.web("#1b2639"));

        primaryStage.setTitle(
                "Đăng nhập - Quản Lý Chi Tiêu Cá Nhân"
        );

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}