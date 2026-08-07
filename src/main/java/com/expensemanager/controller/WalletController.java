package com.expensemanager.controller;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private Label walletCountLabel;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    /**
     * JavaFX tự động gọi sau khi load wallet.fxml.
     */
    @FXML
    private void initialize() {

        /*
         * Tên ví
         */
        nameColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getName()
                        )
        );

        /*
         * Loại ví
         */
        typeColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getType()
                                        .toString()
                        )
        );

        /*
         * Số dư
         */
        balanceColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                expenseManager
                                        .getFormattedBalance(
                                                cellData.getValue()
                                        )
                        )
        );

        /*
         * Sửa / Xóa
         */
        setupActionColumn();

        /*
         * Load dữ liệu.
         */
        refreshWalletTable();
    }

    /**
     * Tạo nút Sửa và Xóa cho từng dòng.
     */
    private void setupActionColumn() {

        actionColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            private final Button editButton =
                                    new Button("Sửa");

                            private final Button deleteButton =
                                    new Button("Xóa");

                            private final HBox buttonBox =
                                    new HBox(
                                            8,
                                            editButton,
                                            deleteButton
                                    );

                            {
                                /*
                                 * Sửa
                                 */
                                editButton.setOnAction(
                                        event -> {

                                            Wallet wallet =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            handleEditWallet(
                                                    wallet
                                            );
                                        }
                                );

                                /*
                                 * Xóa
                                 */
                                deleteButton.setOnAction(
                                        event -> {

                                            Wallet wallet =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            handleDeleteWallet(
                                                    wallet
                                            );
                                        }
                                );
                            }

                            @Override
                            protected void updateItem(
                                    Void item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (empty) {

                                    setGraphic(null);

                                } else {

                                    setGraphic(
                                            buttonBox
                                    );
                                }
                            }
                        }
        );
    }

    /**
     * Mở popup sửa ví.
     */
    private void handleEditWallet(
            Wallet wallet
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/com/expensemanager/view/wallet-form.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            /*
             * Lấy WalletFormController.
             */
            WalletFormController formController =
                    loader.getController();

            /*
             * Truyền Wallet cần sửa.
             */
            formController.setEditingWallet(
                    wallet
            );

            Scene scene =
                    new Scene(root);

            String css =
                    getClass()
                            .getResource(
                                    "/css/styles.css"
                            )
                            .toExternalForm();

            scene.getStylesheets()
                    .add(css);

            Stage stage =
                    new Stage();

            stage.setTitle(
                    "Sửa ví"
            );

            stage.setScene(
                    scene
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setResizable(
                    false
            );

            /*
             * Chờ đóng popup.
             */
            stage.showAndWait();

            /*
             * Refresh bảng sau khi sửa.
             */
            refreshWalletTable();

        } catch (IOException e) {

            System.out.println(
                    "Không thể mở form sửa ví."
            );

            e.printStackTrace();
        }
    }

    /**
     * Xóa ví.
     */
    private void handleDeleteWallet(
            Wallet wallet
    ) {

        Alert confirmAlert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmAlert.setTitle(
                "Xác nhận xóa"
        );

        confirmAlert.setHeaderText(
                "Bạn có chắc muốn xóa ví này?"
        );

        confirmAlert.setContentText(
                wallet.getName()
        );

        Optional<ButtonType> result =
                confirmAlert.showAndWait();

        if (result.isPresent()
                && result.get()
                == ButtonType.OK) {

            try {

                expenseManager.removeWallet(
                        wallet
                );

                refreshWalletTable();

            } catch (Exception e) {

                Alert errorAlert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                errorAlert.setTitle(
                        "Không thể xóa ví"
                );

                errorAlert.setHeaderText(
                        "Xóa ví thất bại"
                );

                String message =
                        e.getMessage();

                if (message == null
                        || message.isBlank()) {

                    message =
                            "Không thể xóa ví.";
                }

                errorAlert.setContentText(
                        message
                );

                errorAlert.showAndWait();

                e.printStackTrace();
            }
        }
    }

    /**
     * Refresh danh sách ví.
     */
    private void refreshWalletTable() {

        List<Wallet> wallets =
                expenseManager.getWallets();

        ObservableList<Wallet> observableWallets =
                FXCollections.observableArrayList(
                        wallets
                );

        walletTable.setItems(
                observableWallets
        );

        walletCountLabel.setText(
                observableWallets.size()
                        + " ví"
        );

        walletTable.refresh();
    }

    /**
     * Mở popup thêm ví.
     */
    @FXML
    private void handleOpenWalletForm() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/com/expensemanager/view/wallet-form.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            Scene scene =
                    new Scene(root);

            String css =
                    getClass()
                            .getResource(
                                    "/css/styles.css"
                            )
                            .toExternalForm();

            scene.getStylesheets()
                    .add(css);

            Stage stage =
                    new Stage();

            stage.setTitle(
                    "Thêm ví"
            );

            stage.setScene(
                    scene
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setResizable(
                    false
            );

            stage.showAndWait();

            /*
             * Refresh sau khi thêm.
             */
            refreshWalletTable();

        } catch (IOException e) {

            System.out.println(
                    "Không thể mở form thêm ví."
            );

            e.printStackTrace();
        }
    }
}