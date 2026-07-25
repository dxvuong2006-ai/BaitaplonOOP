package com.expensemanager.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/** Lớp để hiển thị các hộp thoại thông báo trong ứng dụng. */
public class AlertUtils {

    /** Ngăn không cho khởi tạo đối tượng bên ngoài do các phương thức đều là static. */
    private AlertUtils() {}

    /** Tạo khung hiển thị lên màn hình. */
    private static void showAlert(AlertType type, String title, String message) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (NoClassDefFoundError | Exception e) {
            if (type == AlertType.ERROR) {
                System.err.println(title + ": " + message);
            } else {
                System.out.println(title + ": " + message);
            }
        }
    }

    /** Hiển thị thông báo thông tin lên màn hình. */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /** Hiển thị thông báo lỗi lên màn hình. */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /** Hiển thị thông báo cảnh cáo lên màn hình. */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }
}
