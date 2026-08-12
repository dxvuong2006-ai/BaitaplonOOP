package com.expensemanager.controller;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.service.ExpenseManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller điều khiển màn hình Giao dịch.
 */
public class TransactionController {

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> noteColumn;

    @FXML
    private TableColumn<Transaction, String> walletColumn;

    @FXML
    private TableColumn<Transaction, String> categoryColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, String> amountColumn;

    @FXML
    private TableColumn<Transaction, Void> actionColumn;

    @FXML
    private Label transactionCountLabel;

    @FXML
    private ComboBox<TransactionType> transactionTypeFilter;

    @FXML
    private ComboBox<Category> categoryFilter;

    @FXML
    private ComboBox<Wallet> walletFilter;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    /**
     * JavaFX tự gọi sau khi load FXML.
     */
    @FXML
    private void initialize() {

        transactionTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        setupColumns();

        setupActionColumn();

        setupFilters();

        refreshTransactionTable();
    }

    /**
     * Setup các cột TableView.
     */
    private void setupColumns() {

        dateColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getDate()
                                        .toString()
                        )
        );

        noteColumn.setCellValueFactory(
                cellData -> {

                    String note =
                            cellData
                                    .getValue()
                                    .getNote();

                    return new SimpleStringProperty(
                            note == null
                                    ? ""
                                    : note
                    );
                }
        );

        walletColumn.setCellValueFactory(
                cellData -> {

                    Wallet wallet =
                            cellData
                                    .getValue()
                                    .getWallet();

                    return new SimpleStringProperty(
                            wallet == null
                                    ? ""
                                    : wallet.getName()
                    );
                }
        );

        categoryColumn.setCellValueFactory(
                cellData -> {

                    Category category =
                            cellData
                                    .getValue()
                                    .getCategory();

                    return new SimpleStringProperty(
                            category == null
                                    ? ""
                                    : category.getName()
                    );
                }
        );

        typeColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getType()
                                        .toString()
                        )
        );

        amountColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                formatAmount(
                                        cellData
                                                .getValue()
                                                .getAmount()
                                )
                        )
        );
    }

    /**
     * Format tiền VND ở tầng hiển thị.
     */
    private String formatAmount(
            double amount
    ) {

        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale("vi", "VN")
                );

        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount)
                + " đ";
    }

    /**
     * Setup bộ lọc.
     */
    private void setupFilters() {

        transactionTypeFilter
                .getItems()
                .setAll(TransactionType.values());

        setupComboBoxPlaceholder(transactionTypeFilter, "Loại giao dịch");

        categoryFilter.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(Category category) {
                        if (category == null) {
                            return "Danh mục";
                        }
                        return category.getName();
                    }

                    @Override
                    public Category fromString(String string) {
                        return null;
                    }
                }
        );
        setupComboBoxPlaceholder(categoryFilter, "Danh mục");

        walletFilter.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(Wallet wallet) {
                        if (wallet == null) {
                            return "Ví";
                        }
                        return wallet.getName();
                    }

                    @Override
                    public Wallet fromString(String string) {
                        return null;
                    }
                }
        );
        setupComboBoxPlaceholder(walletFilter, "Ví");

        reloadFilterOptions();
    }

    /**
     * Cấu hình hiển thị nhãn mặc định khi ComboBox có giá trị null
     */
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
                } else if (item instanceof Wallet) {
                    setText(((Wallet) item).getName());
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    /**
     * Reload Wallet / Category hiện tại.
     */
    private boolean isReloadingFilter = false;

    private void reloadFilterOptions() {
        if (isReloadingFilter) return;

        try {
            isReloadingFilter = true;

            Category selectedCategory = categoryFilter.getValue();
            Wallet selectedWallet = walletFilter.getValue();

            categoryFilter.getItems().setAll(expenseManager.getCategories());
            walletFilter.getItems().setAll(expenseManager.getWallets());

            if (selectedCategory != null && categoryFilter.getItems().contains(selectedCategory)) {
                categoryFilter.setValue(selectedCategory);
            } else {
                categoryFilter.setValue(null);
            }

            if (selectedWallet != null && walletFilter.getItems().contains(selectedWallet)) {
                walletFilter.setValue(selectedWallet);
            } else {
                walletFilter.setValue(null);
            }
        } finally {
            isReloadingFilter = false;
        }
    }

    @FXML
    private void handleFilterChanged() {
        if (!isReloadingFilter) {
            refreshTransactionTable();
        }
    }

    /**
     * Reset filter.
     */
    @FXML
    private void handleResetFilters() {

        if (transactionTypeFilter != null) {
            transactionTypeFilter.getSelectionModel().clearSelection();
            transactionTypeFilter.setValue(null);
        }

        if (categoryFilter != null) {
            categoryFilter.getSelectionModel().clearSelection();
            categoryFilter.setValue(null);
        }

        if (walletFilter != null) {
            walletFilter.getSelectionModel().clearSelection();
            walletFilter.setValue(null);
        }

        refreshTransactionTable();
    }

    /**
     * Refresh danh sách.
     */
    private void refreshTransactionTable() {

        List<Transaction> allTransactions =
                expenseManager.getTransactions();

        TransactionType selectedType =
                transactionTypeFilter.getValue();

        Category selectedCategory =
                categoryFilter.getValue();

        Wallet selectedWallet =
                walletFilter.getValue();

        List<Transaction> filteredTransactions =
                new ArrayList<>();

        for (Transaction transaction :
                allTransactions) {

            boolean typeMatches =
                    selectedType == null
                            || transaction.getType()
                            == selectedType;

            boolean categoryMatches =
                    selectedCategory == null
                            || selectedCategory.equals(
                            transaction.getCategory()
                    );

            boolean walletMatches =
                    selectedWallet == null
                            || selectedWallet.equals(
                            transaction.getWallet()
                    );

            if (typeMatches
                    && categoryMatches
                    && walletMatches) {

                filteredTransactions.add(
                        transaction
                );
            }
        }

        ObservableList<Transaction> observableTransactions =
                FXCollections.observableArrayList(
                        filteredTransactions
                );

        transactionTable.setItems(
                observableTransactions
        );

        transactionCountLabel.setText(
                observableTransactions.size()
                        + " giao dịch"
        );

        transactionTable.refresh();
    }

    /**
     * Nút Sửa / Xóa.
     */
    private void setupActionColumn() {

        actionColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            private final Button editButton =
                                    new Button();

                            private final Button deleteButton =
                                    new Button();

                            private final HBox buttonBox =
                                    new HBox(
                                            8,
                                            editButton,
                                            deleteButton
                                    );

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
                                editButton.setOnAction(
                                        event -> {

                                            Transaction transaction =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            handleEditTransaction(
                                                    transaction
                                            );
                                        }
                                );

                                deleteButton.setOnAction(
                                        event -> {

                                            Transaction transaction =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            handleDeleteTransaction(
                                                    transaction
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
     * Mở form thêm.
     */
    @FXML
    private void handleOpenTransactionForm() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/com/expensemanager/view/transaction-form.fxml"
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
                    "Thêm giao dịch"
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

            refreshTransactionTable();

        } catch (IOException e) {

            System.out.println(
                    "Không thể mở form thêm giao dịch."
            );

            e.printStackTrace();
        }
    }

    /**
     * Mở form sửa.
     */
    private void handleEditTransaction(Transaction transaction) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/com/expensemanager/view/transaction-form.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            TransactionFormController formController =
                    loader.getController();

            formController.setEditingTransaction(
                    transaction
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
                    "Sửa giao dịch"
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

            refreshTransactionTable();

        } catch (IOException e) {

            System.out.println(
                    "Không thể mở form sửa giao dịch."
            );

            e.printStackTrace();
        }
    }

    /**
     * Xóa giao dịch.
     */
    private void handleDeleteTransaction(Transaction transaction) {

        Alert confirmAlert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmAlert.setTitle(
                "Xác nhận xóa"
        );

        confirmAlert.setHeaderText(
                "Bạn có chắc muốn xóa giao dịch này?"
        );

        String note =
                transaction.getNote();

        if (note == null
                || note.isBlank()) {

            note =
                    formatAmount(
                            transaction.getAmount()
                    );
        }

        confirmAlert.setContentText(
                note
        );

        Optional<ButtonType> result =
                confirmAlert.showAndWait();

        if (result.isPresent()
                && result.get()
                == ButtonType.OK) {

            try {

                /*
                 * Backend tự xử lý hoàn tác số dư Wallet.
                 */
                expenseManager.removeTransaction(
                        transaction
                );

                refreshTransactionTable();

            } catch (Exception e) {

                String message =
                        e.getMessage();

                if (message == null
                        || message.isBlank()) {

                    message =
                            "Không thể xóa giao dịch.";
                }

                Alert errorAlert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                errorAlert.setTitle(
                        "Không thể xóa giao dịch"
                );

                errorAlert.setHeaderText(
                        "Xóa giao dịch thất bại"
                );

                errorAlert.setContentText(
                        message
                );

                errorAlert.showAndWait();

                e.printStackTrace();
            }
        }
    }
}
