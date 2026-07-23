package com.expensemanager.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Lớp tiện ích (utility class) hiển thị các hộp thoại (dialog) thông báo, cảnh báo,
 * xác nhận và nhập liệu trên giao diện JavaFX.
 */
public final class AlertUtils {

    /** Ngăn không cho khởi tạo đối tượng. */
    private AlertUtils() {
        throw new UnsupportedOperationException("Utility class khong the khoi tao!");
    }

    // ==================== HỘP THOẠI THÔNG BÁO CƠ BẢN ====================

    /** Hiển thị hộp thoại thông tin (VD: thêm giao dịch thành công). */
    public static void showInfo(String title, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, null, content, null);
    }

    /** Hiển thị hộp thoại thông tin có tiêu đề phụ (header) tùy chỉnh. */
    public static void showInfo(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content, null);
    }

    /** Hiển thị hộp thoại báo lỗi chung, không kèm ngoại lệ. */
    public static void showError(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, "Lỗi Hệ Thống", content, null);
    }

    /**
     * Hiển thị hộp thoại báo lỗi kèm thông điệp lấy trực tiếp từ một {@link Exception}.
     * Dùng khi bắt lỗi từ thao tác đọc/ghi file (CsvStorage/JsonStorage), gọi API tỷ giá,
     * hoặc các ngoại lệ nghiệp vụ (VD: rút quá số dư ví, danh mục không tồn tại).
     */
    public static void showError(String title, String context, Throwable ex) {
        String message = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Lỗi không xác định.";
        showAlert(Alert.AlertType.ERROR, title, "Lỗi Hệ Thống", context + "\n" + message, null);
    }

    /** Hiển thị hộp thoại cảnh báo (VD: sắp hết hạn hóa đơn định kỳ). */
    public static void showWarning(String title, String content) {
        showAlert(Alert.AlertType.WARNING, title, "Cảnh Báo", content, null);
    }

    /**
     * Hiển thị cảnh báo vượt hạn mức ngân sách cho một danh mục ({@code Budget.isExceeded}).
     * Chuẩn hóa nội dung thông báo để dùng thống nhất khi ExpenseManager phát hiện
     * chi tiêu trong tháng vượt quá {@code limit} đã đặt.
     */
    public static void showBudgetExceeded(String categoryName, double limit, double spent) {
        String content = String.format(
                "Danh mục \"%s\" đã vượt hạn mức ngân sách!%nHạn mức: %s%nĐã chi: %s%nVượt quá: %s",
                categoryName,
                CurrencyUtils.formatVND(limit),
                CurrencyUtils.formatVND(spent),
                CurrencyUtils.formatVND(spent - limit));
        showAlert(Alert.AlertType.WARNING, "Cảnh Báo Ngân Sách", "Vượt Hạn Mức Chi Tiêu", content, null);
    }

    // ==================== HỘP THOẠI XÁC NHẬN ====================

    /** Trả về true nếu người dùng chọn OK, false nếu chọn Cancel hoặc đóng hộp thoại. */
    public static boolean showConfirmation(String title, String content) {
        return showConfirmation(title, "Xác nhận thao tác", content);
    }

    /** Trả về true nếu người dùng chọn OK, false nếu chọn Cancel hoặc đóng hộp thoại. */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Hộp thoại xác nhận chuyên dụng cho thao tác xóa (giao dịch, ví, danh mục...).
     * Dùng riêng để đảm bảo mọi nơi trong ứng dụng hỏi xác nhận xóa theo cùng một
     * văn phong, tránh xóa nhầm dữ liệu.
     */
    public static boolean confirmDelete(String itemDescription) {
        return showConfirmation(
                "Xác Nhận Xóa",
                "Bạn có chắc chắn muốn xóa " + itemDescription + " không?",
                "Thao tác này không thể hoàn tác.");
    }

    // ==================== HỘP THOẠI NHẬP LIỆU ====================

    /**
     * Hiển thị hộp thoại nhập một dòng văn bản (VD: nhập ghi chú, tên ví/danh mục mới).
     */
    public static Optional<String> showTextInput(String title, String header, String contentLabel,
                                                 String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue == null ? "" : defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(contentLabel);
        return dialog.showAndWait();
    }

    // ==================== HỖ TRỢ ĐA LUỒNG ====================

    /**
     * Hiển thị hộp thoại thông tin an toàn khi được gọi từ một luồng nền (VD: luồng
     * đọc/ghi file lớn, luồng gọi API tỷ giá hối đoái). Tự động chuyển việc hiển thị
     * sang JavaFX Application Thread thông qua {@link Platform#runLater(Runnable)}.
     */
    public static void showInfoLater(String title, String content) {
        Platform.runLater(() -> showInfo(title, content));
    }

    /** Hiển thị hộp thoại báo lỗi an toàn khi được gọi từ một luồng nền. */
    public static void showErrorLater(String title, String content) {
        Platform.runLater(() -> showError(title, content));
    }

    /** Hiển thị hộp thoại báo lỗi kèm ngoại lệ, an toàn khi được gọi từ một luồng nền. */
    public static void showErrorLater(String title, String context, Throwable ex) {
        Platform.runLater(() -> showError(title, context, ex));
    }

    // ==================== HÀM DÙNG CHUNG NỘI BỘ ====================

    /**
     * Tạo và hiển thị một {@link Alert}, có thể gắn owner window để hộp thoại luôn
     * hiện phía trên cửa sổ chính (modal).
     */
    private static void showAlert(Alert.AlertType type, String title, String header, String content,
                                  Window owner) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (owner != null) {
            alert.initOwner(owner);
            alert.initModality(Modality.WINDOW_MODAL);
        }
        alert.showAndWait();
    }
}
