package com.expensemanager.controller;

import com.expensemanager.model.category.Category;
import com.expensemanager.service.ExpenseManager;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/** Controller điều khiển form thêm / sửa danh mục. */
public class CategoryFormController {

    @FXML private Label formTitleLabel;

    @FXML private Label formSubtitleLabel;

    @FXML private TextField categoryNameField;

    @FXML private TextArea descriptionArea;

    private final ExpenseManager manager = ExpenseManager.getInstance();

    /** null = Thêm mới. có giá trị = Sửa. */
    private Category editingCategory;

    /** Đưa Category hiện tại vào form khi sửa. */
    public void setEditingCategory(Category category) {
        this.editingCategory = category;

        formTitleLabel.setText("Sửa danh mục");

        formSubtitleLabel.setText("Cập nhật thông tin danh mục");

        categoryNameField.setText(category.getName());

        String description = category.getDescription();

        descriptionArea.setText(description == null ? "" : description);
    }

    /** Lưu. */
    @FXML
    private void handleSave() {
        try {
            String name = categoryNameField.getText().trim();

            if (name.isEmpty()) {
                showError("Tên danh mục không được để trống.");
                return;
            }

            String description = descriptionArea.getText().trim();

            int userId = manager.getCurrentUserId();
            /*
             * ========================
             * THÊM
             * ========================
             */
            if (editingCategory == null) {
                String id = UUID.randomUUID().toString();

                Category category = new Category(id, name, description, userId);

                manager.addCategory(category);

                showSuccess("Đã thêm danh mục thành công.");
            } else {
                /*
                 * ========================
                 * SỬA
                 * ========================
                 */
                Category updatedCategory =
                        new Category(editingCategory.getId(), name, description, userId);

                manager.updateCategory(editingCategory, updatedCategory);

                showSuccess("Đã cập nhật danh mục thành công.");
            }

            closeForm();

        } catch (Exception e) {
            String message = e.getMessage();

            if (message == null || message.isBlank()) {
                if (editingCategory == null) {
                    message = "Không thể thêm danh mục.";
                } else {
                    message = "Không thể cập nhật danh mục.";
                }
            }

            showError(message);
        }
    }

    /** Hủy. */
    @FXML
    private void handleCancel() {
        closeForm();
    }

    /** Đóng popup. */
    private void closeForm() {
        Stage stage = (Stage) categoryNameField.getScene().getWindow();
        stage.close();
    }

    /** Hiển thị lỗi. */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dữ liệu không hợp lệ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Hiển thị thành công. */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
