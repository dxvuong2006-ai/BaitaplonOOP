package com.expensemanager.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Lớp chịu trách nhiệm khởi động cửa sổ giao diện đồ hoạ (GUI) JavaFX.
 * Kế thừa từ javafx.application.Application theo đúng quy định bắt buộc
 * của framework JavaFX để có vòng đời (lifecycle) quản lý cửa sổ.
 */
public class AppLauncher extends Application {

    /**
     * Hàm start() được JavaFX tự động gọi sau khi khởi tạo xong môi trường
     * đồ hoạ. Đây là nơi nạp giao diện chính (main.fxml) và hiển thị lên
     * màn hình.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/expensemanager/view/main.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Quản Lý Chi Tiêu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}