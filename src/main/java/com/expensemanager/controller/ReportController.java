package com.expensemanager.controller;

import com.expensemanager.model.enums.ReportType;
import com.expensemanager.service.ExpenseManager;

import java.io.File;
import java.time.LocalDate;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ReportController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<ReportType> reportTypeComboBox;

    @FXML
    private Button exportButton;

    @FXML
    private ProgressIndicator exportProgressIndicator;

    @FXML
    private Label exportStatusLabel;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    @FXML
    private void initialize() {

        reportTypeComboBox
                .getItems()
                .setAll(ReportType.values());

        reportTypeComboBox.setValue(ReportType.PDF);

        startDatePicker.setValue(
                LocalDate.now().withDayOfMonth(1)
        );

        endDatePicker.setValue(LocalDate.now());

        setExportingState(false);
    }

    /**
     * Xuất báo cáo.
     *
     * FileChooser vẫn chạy trên JavaFX Application Thread.
     * Phần tạo file báo cáo chạy ở Background Thread
     * để không làm treo giao diện.
     */
    @FXML
    private void handleExportReport() {

        LocalDate startDate =
                startDatePicker.getValue();

        LocalDate endDate =
                endDatePicker.getValue();

        ReportType type =
                reportTypeComboBox.getValue();

        if (startDate == null
                || endDate == null
                || type == null) {

            showError(
                    "Vui lòng chọn đầy đủ khoảng thời gian "
                            + "và định dạng báo cáo."
            );

            return;
        }

        /*
         * FileChooser phải chạy trên UI Thread.
         */
        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle("Lưu báo cáo");

        String extension =
                type == ReportType.PDF
                        ? "pdf"
                        : "xlsx";

        fileChooser.setInitialFileName(
                "bao-cao." + extension
        );

        fileChooser
                .getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                type.getDisplayName(),
                                "*." + extension
                        )
                );

        Stage stage =
                (Stage) startDatePicker
                        .getScene()
                        .getWindow();

        File file =
                fileChooser.showSaveDialog(stage);

        /*
         * Người dùng bấm Cancel.
         */
        if (file == null) {
            return;
        }

        /*
         * =================================================
         * MULTITHREADING
         * =================================================
         *
         * Task.call() chạy trên Background Thread.
         */
        Task<File> exportTask =
                new Task<>() {

                    @Override
                    protected File call()
                            throws Exception {

                        /*
                         * Logic xuất báo cáo cũ vẫn giữ nguyên.
                         */
                        File exportedFile =
                                expenseManager.exportReport(
                                        startDate,
                                        endDate,
                                        type,
                                        file
                                );

                        /*
                         * Giữ trạng thái loading tối thiểu
                         * 1.5 giây để người dùng nhìn thấy rõ
                         * background task đang chạy.
                         *
                         * Thread.sleep nằm trong Background Thread,
                         * KHÔNG làm treo JavaFX UI Thread.
                         */
                        Thread.sleep(1500);

                        return exportedFile;
                    }
                };

        /*
         * Chuyển UI sang trạng thái đang xuất.
         */
        setExportingState(true);

        /*
         * Khi xuất thành công.
         *
         * Handler này được JavaFX chạy lại
         * trên JavaFX Application Thread.
         */
        exportTask.setOnSucceeded(event -> {

            setExportingState(false);

            showSuccess(
                    "Xuất báo cáo thành công: "
                            + file.getName()
            );
        });

        /*
         * Khi xuất thất bại.
         */
        exportTask.setOnFailed(event -> {

            setExportingState(false);

            Throwable exception =
                    exportTask.getException();

            String message =
                    exception == null
                            ? null
                            : exception.getMessage();

            showError(
                    message == null
                            || message.isBlank()
                            ? "Không thể xuất báo cáo."
                            : message
            );
        });

        /*
         * Tạo Thread chạy Task.
         */
        Thread exportThread =
                new Thread(exportTask);

        /*
         * Khi app đóng thì thread này
         * không giữ JVM tiếp tục chạy.
         */
        exportThread.setDaemon(true);

        /*
         * Đặt tên thread để dễ giải thích/debug.
         */
        exportThread.setName(
                "report-export-thread"
        );

        /*
         * Bắt đầu Background Thread.
         */
        exportThread.start();
    }

    /**
     * Bật / tắt trạng thái xuất báo cáo.
     */
    private void setExportingState(
            boolean exporting) {

        exportButton.setDisable(exporting);

        startDatePicker.setDisable(exporting);
        endDatePicker.setDisable(exporting);
        reportTypeComboBox.setDisable(exporting);

        exportProgressIndicator.setVisible(exporting);
        exportProgressIndicator.setManaged(exporting);

        exportStatusLabel.setVisible(exporting);
        exportStatusLabel.setManaged(exporting);

        if (exporting) {
            exportButton.setText("Đang xuất...");
        } else {
            exportButton.setText("Xuất báo cáo");
        }
    }

    /**
     * Hiển thị lỗi.
     */
    private void showError(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * Hiển thị thành công.
     */
    private void showSuccess(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}