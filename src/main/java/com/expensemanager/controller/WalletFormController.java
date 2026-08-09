package com.expensemanager.controller;

import com.expensemanager.factory.model.WalletFactory;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.service.ExpenseManager;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/** Controller điều khiển form thêm / sửa ví. */
public class WalletFormController {

    @FXML private TextField walletNameField;

    @FXML private ComboBox<WalletType> walletTypeComboBox;

    @FXML private TextField initialBalanceField;

    @FXML private TextField transactionFeeField;

    @FXML private TextArea noteArea;

    private final ExpenseManager manager = ExpenseManager.getInstance();

    /** null = chế độ Thêm, có giá trị = chế độ Sửa. */
    private Wallet editingWallet;

    /** JavaFX tự động gọi sau khi FXML được nạp. */
    @FXML
    private void initialize() {
        walletTypeComboBox.getItems().setAll(WalletType.values());
    }

    /** Nhận Wallet cần sửa từ WalletController và đổ dữ liệu cũ lên form. */
    public void setEditingWallet(Wallet wallet) {
        this.editingWallet = wallet;

        walletNameField.setText(wallet.getName());

        walletTypeComboBox.setValue(wallet.getType());

        initialBalanceField.setText(String.valueOf(wallet.getBalance()));

        double extraFee = 0.0;

        if (wallet instanceof BankAccount bankAccount) {
            extraFee = bankAccount.getTransactionFee();
        } else if (wallet instanceof EWallet eWallet) {
            extraFee = eWallet.getFeePercent();
        }

        transactionFeeField.setText(String.valueOf(extraFee));
    }

    /** Hủy và đóng form. */
    @FXML
    private void handleCancel() {
        closeForm();
    }

    /** Thêm mới hoặc cập nhật ví. */
    @FXML
    private void handleSave() {
        try {
            String name = walletNameField.getText().trim();

            if (name.isEmpty()) {
                showError("Tên ví không được để trống.");
                return;
            }

            WalletType type = walletTypeComboBox.getValue();

            if (type == null) {
                showError("Vui lòng chọn loại ví.");
                return;
            }

            double balance = parseNumber(initialBalanceField.getText(), "Số dư ban đầu");

            if (balance < 0) {
                showError("Số dư ban đầu không được âm.");
                return;
            }

            double extraFee = 0.0;

            String feeText = transactionFeeField.getText().trim();

            if (!feeText.isEmpty()) {
                extraFee = parseNumber(feeText, "Phí giao dịch");

                if (extraFee < 0) {
                    showError("Phí giao dịch không được âm.");
                    return;
                }
            }

            int userId = manager.getCurrentUserId();
            /*
             * ==========================
             * CHẾ ĐỘ THÊM
             * ==========================
             */
            if (editingWallet == null) {
                String id = UUID.randomUUID().toString();

                Wallet newWallet = WalletFactory.createWallet(id, name, balance, type, extraFee, userId);

                manager.addWallet(newWallet);

                showSuccess("Đã thêm ví thành công.");
            } else {
                /*
                 * ==========================
                 * CHẾ ĐỘ SỬA
                 * ==========================
                 */

                /*
                 * Giữ nguyên ID của ví cũ.
                 */
                String id = editingWallet.getId();

                Wallet updatedWallet = WalletFactory.createWallet(id, name, balance, type, extraFee, userId);

                manager.updateWallet(editingWallet, updatedWallet);

                showSuccess("Đã cập nhật ví thành công.");
            }

            closeForm();

        } catch (Exception e) {
            String message = e.getMessage();

            if (message == null || message.isBlank()) {
                if (editingWallet == null) {
                    message = "Không thể thêm ví.";
                } else {
                    message = "Không thể cập nhật ví.";
                }
            }

            showError(message);
        }
    }

    /** Chuyển chuỗi nhập vào thành số. */
    private double parseNumber(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là một số hợp lệ.");
        }
    }

    /** Hiển thị lỗi. */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dữ liệu không hợp lệ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Hiển thị thông báo thành công. */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Đóng popup hiện tại. */
    private void closeForm() {
        Stage stage = (Stage) walletNameField.getScene().getWindow();
        stage.close();
    }
}
