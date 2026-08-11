package com.expensemanager.controller;

import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.service.ExpenseManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/**
 * Controller điều khiển màn hình Ví.
 */
public class WalletController {

    @FXML
    private TableView<Wallet> walletTable;

    @FXML
    private TableColumn<Wallet, String> nameColumn;

    @FXML
    private TableColumn<Wallet, String> typeColumn;

    @FXML
    private TableColumn<Wallet, String> balanceColumn;

    @FXML
    private TableColumn<Wallet, String> currencyColumn;

    @FXML
    private TableColumn<Wallet, String> noteColumn;

    @FXML
    private TableColumn<Wallet, Void> actionColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<WalletType> walletTypeFilter;

    @FXML
    private Label walletCountLabel;

    private final ExpenseManager expenseManager = ExpenseManager.getInstance();

    @FXML
    private void initialize() {
        walletTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        // 1. Cấu hình các cột cho TableView
        nameColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getName())
        );

        typeColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getType().toString())
        );

        balanceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        expenseManager.getFormattedBalance(cellData.getValue())
                )
        );

        setupActionColumn();

        // 2. Cấu hình ComboBox Bộ lọc loại ví
        if (walletTypeFilter != null) {
            walletTypeFilter.getItems().setAll(WalletType.values());

            // Hiển thị chữ "Loại ví" khi giá trị null (đặt lại)
            walletTypeFilter.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(WalletType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Loại ví");
                    } else {
                        setText(item.toString());
                    }
                }
            });

            // Lắng nghe sự kiện lọc theo loại
            walletTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> refreshWalletTable());
        }

        // 3. Lắng nghe sự kiện lọc theo tên ví
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshWalletTable());
        }

        // 4. Tải dữ liệu ban đầu
        refreshWalletTable();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(
                column -> new TableCell<>() {
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox buttonBox = new HBox(8, editButton, deleteButton);

                    {
                        // =========================
                        // ICON SỬA
                        // =========================

                        ImageView editIcon =
                                new ImageView(
                                        new Image(
                                                getClass()
                                                        .getResource(
                                                                "/images/sua.png"
                                                        )
                                                        .toExternalForm()
                                        )
                                );

                        editIcon.setFitWidth(16);
                        editIcon.setFitHeight(16);
                        editIcon.setPreserveRatio(true);
                        editIcon.setSmooth(true);

                        editButton.setGraphic(editIcon);


                        // =========================
                        // ICON XÓA
                        // =========================

                        ImageView deleteIcon =
                                new ImageView(
                                        new Image(
                                                getClass()
                                                        .getResource(
                                                                "/images/trash.png"
                                                        )
                                                        .toExternalForm()
                                        )
                                );

                        deleteIcon.setFitWidth(16);
                        deleteIcon.setFitHeight(16);
                        deleteIcon.setPreserveRatio(true);
                        deleteIcon.setSmooth(true);

                        deleteButton.setGraphic(deleteIcon);
                        editButton.setOnAction(event -> {
                            Wallet wallet = getTableView().getItems().get(getIndex());
                            handleEditWallet(wallet);
                        });

                        deleteButton.setOnAction(event -> {
                            Wallet wallet = getTableView().getItems().get(getIndex());
                            handleDeleteWallet(wallet);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttonBox);
                        }
                    }
                }
        );
    }

    private void handleEditWallet(Wallet wallet) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/expensemanager/view/wallet-form.fxml")
            );
            Parent root = loader.load();

            WalletFormController formController = loader.getController();
            formController.setEditingWallet(wallet);

            Scene scene = new Scene(root);
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.setTitle("Sửa ví");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            refreshWalletTable();
        } catch (IOException e) {
            System.out.println("Không thể mở form sửa ví.");
            e.printStackTrace();
        }
    }

    private void handleDeleteWallet(Wallet wallet) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa ví này?");
        confirmAlert.setContentText(wallet.getName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                expenseManager.removeWallet(wallet);
                refreshWalletTable();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Không thể xóa ví");
                errorAlert.setHeaderText("Xóa ví thất bại");
                String message = e.getMessage();
                if (message == null || message.isBlank()) {
                    message = "Không thể xóa ví.";
                }
                errorAlert.setContentText(message);
                errorAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    /**
     * Lọc và làm mới bảng ví.
     */
    private void refreshWalletTable() {
        List<Wallet> allWallets = expenseManager.getWallets();
        List<Wallet> filteredWallets = new ArrayList<>();

        String keyword = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim().toLowerCase()
                : "";

        WalletType selectedType = (walletTypeFilter != null)
                ? walletTypeFilter.getValue()
                : null;

        for (Wallet wallet : allWallets) {
            boolean matchesName = keyword.isEmpty()
                    || (wallet.getName() != null && wallet.getName().toLowerCase().contains(keyword));

            boolean matchesType = (selectedType == null)
                    || (wallet.getType() == selectedType);

            if (matchesName && matchesType) {
                filteredWallets.add(wallet);
            }
        }

        ObservableList<Wallet> observableWallets = FXCollections.observableArrayList(filteredWallets);
        walletTable.setItems(observableWallets);
        walletCountLabel.setText(observableWallets.size() + " ví");
        walletTable.refresh();
    }

    /**
     * Sự kiện nút "Đặt lại" bộ lọc.
     */
    @FXML
    private void handleResetFilters() {
        if (searchField != null) {
            searchField.clear();
        }
        if (walletTypeFilter != null) {
            // Xóa lựa chọn hiển thị để khôi phục chữ mờ (promptText)
            walletTypeFilter.getSelectionModel().clearSelection();
            walletTypeFilter.setValue(null);
        }
        refreshWalletTable();
    }

    @FXML
    private void handleOpenWalletForm() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/expensemanager/view/wallet-form.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root);
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.setTitle("Thêm ví");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            refreshWalletTable();
        } catch (IOException e) {
            System.out.println("Không thể mở form thêm ví.");
            e.printStackTrace();
        }
    }
}
