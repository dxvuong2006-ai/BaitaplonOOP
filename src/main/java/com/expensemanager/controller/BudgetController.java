package com.expensemanager.controller;

import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.service.BudgetService;
import com.expensemanager.service.ExpenseManager;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/** Controller điều khiển màn hình Quản lý Ngân sách. */
public class BudgetController {

    @FXML private TableView<Budget> budgetTable;

    @FXML private TableColumn<Budget, String> categoryColumn;

    @FXML private TableColumn<Budget, String> limitColumn;

    @FXML private TableColumn<Budget, String> spentColumn;

    @FXML private TableColumn<Budget, String> remainingColumn;

    @FXML private TableColumn<Budget, String> periodColumn;

    @FXML private TableColumn<Budget, String> usageColumn;

    @FXML private TableColumn<Budget, Void> actionColumn;

    @FXML private Label budgetCountLabel;

    @FXML private ComboBox<Category> categoryFilter;

    @FXML private ComboBox<Period> periodFilter;

    private final ExpenseManager expenseManager = ExpenseManager.getInstance();
    private final BudgetService budgetService = expenseManager.getBudgetService();

    /** Khởi tạo giao diện và gán các sự kiện lắng nghe. */
    @FXML
    private void initialize() {
        setupColumns();
        setupFilters();
        setupActionColumn();

        categoryFilter
                .valueProperty()
                .addListener((observable, oldValue, newValue) -> refreshBudgetTable());

        periodFilter
                .valueProperty()
                .addListener((observable, oldValue, newValue) -> refreshBudgetTable());

        refreshBudgetTable();
    }

    /** Cấu hình hiển thị dữ liệu cho các cột trong bảng. */
    private void setupColumns() {
        int userId = expenseManager.getCurrentUserId();
        categoryColumn.setCellValueFactory(
                cellData -> {
                    Category category = cellData.getValue().getCategory();
                    return new SimpleStringProperty(category == null ? "" : category.getName());
                });

        limitColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(formatMoney(cellData.getValue().getLimitAmount())));

        spentColumn.setCellValueFactory(
                cellData -> {
                    Budget budget = cellData.getValue();
                    double remaining = budgetService.getRemainingBudget(budget, userId);
                    double spent = budget.getLimitAmount() - remaining;
                    return new SimpleStringProperty(formatMoney(spent));
                });

        remainingColumn.setCellValueFactory(
                cellData -> {
                    Budget budget = cellData.getValue();
                    double remaining = budgetService.getRemainingBudget(budget, userId);
                    return new SimpleStringProperty(formatMoney(remaining));
                });

        periodColumn.setCellValueFactory(
                cellData -> {
                    Period period = cellData.getValue().getPeriod();
                    return new SimpleStringProperty(period == null ? "" : period.toString());
                });

        usageColumn.setCellValueFactory(
                cellData -> {
                    Budget budget = cellData.getValue();
                    double usage = budgetService.getUsagePercentage(budget, userId);
                    return new SimpleStringProperty(String.format(Locale.US, "%.1f%%", usage));
                });
    }

    /** Cấu hình các bộ lọc danh mục và chu kỳ. */
    private void setupFilters() {
        categoryFilter.setConverter(
                new StringConverter<>() {
                    @Override
                    public String toString(Category category) {
                        return category == null ? "Danh mục" : category.getName();
                    }

                    @Override
                    public Category fromString(String string) {
                        return null;
                    }
                });

        categoryFilter.getItems().setAll(expenseManager.getCategories());
        periodFilter.getItems().setAll(Period.values());

        // Cấu hình nhãn mờ hiển thị khi null
        setupComboBoxPlaceholder(categoryFilter, "Danh mục");
        setupComboBoxPlaceholder(periodFilter, "Chu kỳ");
    }

    /** Cấu hình hiển thị nhãn mặc định khi ComboBox có giá trị null */
    private <T> void setupComboBoxPlaceholder(ComboBox<T> comboBox, String placeholder) {
        if (comboBox == null) return;
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(placeholder);
                } else if (item instanceof Category) {
                    setText(((Category) item).getName());
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    /** Cấu hình cột thao tác (Sửa / Xóa) cho từng dòng trong bảng. */
    private void setupActionColumn() {
        actionColumn.setCellFactory(
                column ->
                        new TableCell<>() {
                            private final Button editButton = new Button("Sửa");
                            private final Button deleteButton = new Button("Xóa");
                            private final HBox buttonBox = new HBox(8, editButton, deleteButton);

                            {
                                editButton.setOnAction(
                                        event -> {
                                            if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                                                return;
                                            }
                                            Budget budget = getTableView().getItems().get(getIndex());
                                            handleEditBudget(budget);
                                        });

                                deleteButton.setOnAction(
                                        event -> {
                                            if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                                                return;
                                            }
                                            Budget budget = getTableView().getItems().get(getIndex());
                                            handleDeleteBudget(budget);
                                        });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(empty ? null : buttonBox);
                            }
                        });
    }

    /** Tải lại dữ liệu bảng ngân sách dựa trên bộ lọc. */
    private void refreshBudgetTable() {
        if (budgetTable == null) {
            return;
        }

        List<Budget> allBudgets = expenseManager.getBudgets();
        Category selectedCategory = categoryFilter.getValue();
        Period selectedPeriod = periodFilter.getValue();

        List<Budget> filteredBudgets = new ArrayList<>();

        for (Budget budget : allBudgets) {
            boolean categoryMatches =
                    selectedCategory == null || selectedCategory.equals(budget.getCategory());
            boolean periodMatches =
                    selectedPeriod == null || selectedPeriod == budget.getPeriod();

            if (categoryMatches && periodMatches) {
                filteredBudgets.add(budget);
            }
        }

        budgetTable.setItems(FXCollections.observableArrayList(filteredBudgets));
        budgetCountLabel.setText(filteredBudgets.size() + " ngân sách");
        budgetTable.refresh();
    }

    /** Xóa tất cả bộ lọc hiện tại. */
    @FXML
    private void handleResetFilters() {
        if (categoryFilter != null) {
            categoryFilter.getSelectionModel().clearSelection();
            categoryFilter.setValue(null);
        }
        if (periodFilter != null) {
            periodFilter.getSelectionModel().clearSelection();
            periodFilter.setValue(null);
        }
        refreshBudgetTable();
    }

    /** Mở popup thêm ngân sách mới. */
    @FXML
    private void handleOpenBudgetForm() {
        openBudgetForm(null);
    }

    /** Mở popup chỉnh sửa ngân sách. */
    private void handleEditBudget(Budget budget) {
        openBudgetForm(budget);
    }

    /** Mở cửa sổ Form Ngân sách (Thêm/Sửa). */
    private void openBudgetForm(Budget budget) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/com/expensemanager/view/budget-form.fxml"));
            Parent root = loader.load();

            BudgetFormController formController = loader.getController();
            if (budget != null) {
                formController.setEditingBudget(budget);
            }

            Scene scene = new Scene(root);
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.setTitle(budget == null ? "Thêm ngân sách" : "Sửa ngân sách");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            reloadCategories();
            refreshBudgetTable();

        } catch (IOException e) {
            System.err.println("Không thể mở form ngân sách.");
            e.printStackTrace();
        }
    }

    /** Cập nhật lại danh sách danh mục trong combobox lọc. */
    private void reloadCategories() {
        Category selectedCategory = categoryFilter.getValue();
        categoryFilter.getItems().setAll(expenseManager.getCategories());

        if (selectedCategory != null && categoryFilter.getItems().contains(selectedCategory)) {
            categoryFilter.setValue(selectedCategory);
        }
    }

    /** Xử lý xóa ngân sách có xác nhận. */
    private void handleDeleteBudget(Budget budget) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc muốn xóa ngân sách này?");

        if (budget.getCategory() != null) {
            alert.setContentText(budget.getCategory().getName());
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                expenseManager.removeBudget(budget);
                refreshBudgetTable();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    /** Format hiển thị tiền tệ VNĐ. */
    private String formatMoney(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount) + " đ";
    }

    /** Hiển thị hộp thoại báo lỗi. */
    private void showError(String message) {
        if (message == null || message.isBlank()) {
            message = "Không thể thực hiện thao tác.";
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
