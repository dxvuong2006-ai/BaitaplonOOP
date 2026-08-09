package com.expensemanager.controller;

import com.expensemanager.model.enums.ReportType;
import com.expensemanager.service.ExpenseManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class ReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<ReportType> reportTypeComboBox;

    private final ExpenseManager expenseManager = ExpenseManager.getInstance();

    @FXML
    private void initialize() {
        reportTypeComboBox.getItems().setAll(ReportType.values());
        reportTypeComboBox.setValue(ReportType.PDF);
        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        endDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleExportReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        ReportType type = reportTypeComboBox.getValue();

        if (startDate == null || endDate == null || type == null) {
            showError("Vui lòng chọn đầy đủ khoảng thời gian và định dạng báo cáo.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        String extension = type == ReportType.PDF ? "pdf" : "xlsx";
        fileChooser.setInitialFileName("bao-cao." + extension);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        type.getDisplayName(), "*." + extension));

        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return; // Người dùng hủy chọn nơi lưu
        }

        try {
            expenseManager.exportReport(startDate, endDate, type, file);
            showSuccess("Xuất báo cáo thành công: " + file.getName());
        } catch (Exception e) {
            String message = e.getMessage();
            showError(message == null || message.isBlank()
                    ? "Không thể xuất báo cáo." : message);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
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
