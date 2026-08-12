package com.expensemanager.controller;

import com.expensemanager.factory.model.TransactionFactory;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.service.ExpenseManager;
import java.time.LocalDate;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/** Controller điều khiển form thêm / sửa giao dịch. */
public class TransactionFormController {

    @FXML
    private Label formTitleLabel;

    @FXML
    private Label formSubtitleLabel;

    @FXML
    private ComboBox<TransactionType> transactionTypeComboBox;

    @FXML
    private TextField amountField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Wallet> walletComboBox;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private VBox incomeFieldsBox;

    @FXML
    private VBox paymentFieldsBox;

    @FXML
    private VBox periodFieldsBox;

    @FXML
    private TextField sourceField;

    @FXML
    private TextField paymentMethodField;

    @FXML
    private ComboBox<Period> periodComboBox;

    @FXML
    private TextArea noteArea;

    private final ExpenseManager manager = ExpenseManager.getInstance();

    /** null = Thêm. Có giá trị = Sửa. */
    private Transaction editingTransaction;

    /** Khởi tạo form. */
    @FXML
    private void initialize() {
        transactionTypeComboBox.getItems().setAll(TransactionType.values());
        walletComboBox.getItems().setAll(manager.getWallets());
        categoryComboBox.getItems().setAll(manager.getCategories());
        periodComboBox.getItems().setAll(Period.values());

        walletComboBox.setConverter(
                new StringConverter<>() {
                    @Override
                    public String toString(Wallet wallet) {
                        return wallet == null ? "" : wallet.getName();
                    }

                    @Override
                    public Wallet fromString(String string) {
                        return null;
                    }
                });

        categoryComboBox.setConverter(
                new StringConverter<>() {
                    @Override
                    public String toString(Category category) {
                        return category == null ? "" : category.getName();
                    }

                    @Override
                    public Category fromString(String string) {
                        return null;
                    }
                });

        datePicker.setValue(LocalDate.now());
        transactionTypeComboBox.setValue(TransactionType.INCOME);

        transactionTypeComboBox
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                updateDynamicFields(newValue));

        updateDynamicFields(TransactionType.INCOME);
    }

    /** Ẩn / hiện field theo loại giao dịch. */
    private void updateDynamicFields(TransactionType type) {

        // Income: chỉ hiện Nguồn thu
        boolean income = (type == TransactionType.INCOME);

        // Expense thường: hiện Phương thức thanh toán
        boolean expense = (type == TransactionType.EXPENSE);

        // Recurring Expense: chỉ hiện Chu kỳ,
        // không hiện Phương thức thanh toán
        boolean recurring = (type == TransactionType.RECURRING_EXPENSE);

        incomeFieldsBox.setVisible(income);
        incomeFieldsBox.setManaged(income);

        paymentFieldsBox.setVisible(false);
        paymentFieldsBox.setManaged(false);

        periodFieldsBox.setVisible(recurring);
        periodFieldsBox.setManaged(recurring);
    }

    /** Nhận Transaction đang sửa. */
    public void setEditingTransaction(Transaction transaction) {
        this.editingTransaction = transaction;

        formTitleLabel.setText("Sửa giao dịch");
        formSubtitleLabel.setText("Cập nhật thông tin giao dịch");

        transactionTypeComboBox.setValue(transaction.getType());
        amountField.setText(String.valueOf(transaction.getAmount()));
        datePicker.setValue(transaction.getDate());
        walletComboBox.setValue(transaction.getWallet());
        categoryComboBox.setValue(transaction.getCategory());

        String note = transaction.getNote();
        noteArea.setText(note == null ? "" : note);

        if (transaction instanceof Income income) {
            String source = income.getSource();
            sourceField.setText(source == null ? "" : source);
        }

        if (transaction instanceof Expense expense) {
            String paymentMethod = expense.getPaymentMethod();
            paymentMethodField.setText(paymentMethod == null ? "" : paymentMethod);
        }

        if (transaction instanceof RecurringExpense recurringExpense) {
            periodComboBox.setValue(recurringExpense.getPeriod());
        }

        updateDynamicFields(transaction.getType());
    }

    /** Lưu giao dịch. */
    @FXML
    private void handleSave() {
        try {
            TransactionType type = transactionTypeComboBox.getValue();

            if (type == null) {
                showError("Vui lòng chọn loại giao dịch.");
                return;
            }

            double amount = parseAmount(amountField.getText());

            if (amount <= 0) {
                showError("Số tiền phải lớn hơn 0.");
                return;
            }

            LocalDate date = datePicker.getValue();

            if (date == null) {
                showError("Vui lòng chọn ngày giao dịch.");
                return;
            }

            Wallet wallet = walletComboBox.getValue();

            if (wallet == null) {
                showError("Vui lòng chọn ví.");
                return;
            }

            Category category = categoryComboBox.getValue();

            if (category == null) {
                showError("Vui lòng chọn danh mục.");
                return;
            }

            String note = noteArea.getText().trim();
            String source = sourceField.getText().trim();
            String paymentMethod = paymentMethodField.getText().trim();
            Period period = periodComboBox.getValue();

            if (type == TransactionType.RECURRING_EXPENSE && period == null) {
                showError("Vui lòng chọn chu kỳ.");
                return;
            }

            String id =
                    (editingTransaction == null)
                            ? UUID.randomUUID().toString()
                            : editingTransaction.getId();

            int userId = manager.getCurrentUserId();

            // Khi tạo mới hoặc lưu/sửa khoản chi định kỳ:
// Ép reset nextDueDate về đúng ngày bắt đầu chọn trên form và kích hoạt active = true
            LocalDate nextDueDate = date;
            boolean active = true;

            Transaction transaction =
                    TransactionFactory.createTransaction(
                            type,
                            id,
                            amount,
                            date,
                            note,
                            category,
                            wallet,
                            source,
                            paymentMethod,
                            period,
                            userId);

            if (editingTransaction == null) {
                manager.addTransaction(transaction);
            } else {
                manager.updateTransaction(editingTransaction, transaction);
            }

// Gọi quét và xử lý các kỳ đến hạn ngay lập tức
            if (type == TransactionType.RECURRING_EXPENSE) {
                manager.processDueExpenses();
            }

            showSuccess("Đã lưu giao dịch thành công.");
            closeForm();

        } catch (Exception e) {
            String message = e.getMessage();

            if (message == null || message.isBlank()) {
                message =
                        (editingTransaction == null)
                                ? "Không thể thêm giao dịch."
                                : "Không thể cập nhật giao dịch.";
            }

            showError(message);
        }
    }

    /** Chuyển số tiền nhập thành double. */
    private double parseAmount(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Số tiền không được để trống.");
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Số tiền phải là một số hợp lệ.");
        }
    }

    /** Hủy. */
    @FXML
    private void handleCancel() {
        closeForm();
    }

    /** Đóng popup. */
    private void closeForm() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }

    /** Lỗi. */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dữ liệu không hợp lệ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Thành công. */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}