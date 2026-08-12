package com.expensemanager.controller;

import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.service.ExpenseManager;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/** Controller điều khiển form thêm / sửa ngân sách. */
public class BudgetFormController {

    @FXML private Label titleLabel;

    @FXML private ComboBox<Category> categoryComboBox;

    @FXML private TextField limitField;

    @FXML private ComboBox<Period> periodComboBox;

    private final ExpenseManager manager = ExpenseManager.getInstance();

    private Budget editingBudget;

    @FXML
    private void initialize() {
        categoryComboBox.getItems().setAll(manager.getCategories());

        categoryComboBox.setConverter(
                new StringConverter<>() {
                    @Override
                    public String toString(Category category) {
                        if (category == null) {
                            return "";
                        }
                        return category.getName();
                    }

                    @Override
                    public Category fromString(String string) {
                        return null;
                    }
                });

        periodComboBox.getItems().setAll(Period.values());
    }

    public void setEditingBudget(Budget budget) {
        this.editingBudget = budget;
        titleLabel.setText("Sửa ngân sách");
        if (budget.getCategory() != null) {
            categoryComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(budget.getCategory().getId()))
                    .findFirst()
                    .ifPresent(categoryComboBox::setValue);
        }

        limitField.setText(String.valueOf(budget.getLimitAmount()));
        periodComboBox.setValue(budget.getPeriod());
    }

    @FXML
    private void handleSave() {
        try {
            Category category = categoryComboBox.getValue();
            if (category == null) {
                showError("Vui lòng chọn danh mục.");
                return;
            }
            double limitAmount = parseLimitAmount(limitField.getText());
            if (limitAmount <= 0) {
                showError("Hạn mức phải lớn hơn 0.");
                return;
            }
            Period period = periodComboBox.getValue();
            if (period == null) {
                showError("Vui lòng chọn chu kỳ.");
                return;
            }
            int userId = manager.getCurrentUserId();
            if (editingBudget == null) {
                String id = UUID.randomUUID().toString();
                Budget budget = new Budget(id, category, limitAmount, period, userId);
                manager.addBudget(budget);
                showSuccess("Đã thêm ngân sách thành công.");
            } else {
                Budget updatedBudget =
                        new Budget(editingBudget.getId(), category, limitAmount, period, userId);
                manager.updateBudget(editingBudget, updatedBudget);
                showSuccess("Đã cập nhật ngân sách thành công.");
            }
            closeForm();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null || message.isBlank()) {
                message =
                        editingBudget == null ? "Không thể thêm ngân sách." : "Không thể cập nhật ngân sách.";
            }
            showError(message);
        }
    }

    private double parseLimitAmount(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Hạn mức không được để trống.");
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Hạn mức phải là một số hợp lệ.");
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) limitField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dữ liệu không hợp lệ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
