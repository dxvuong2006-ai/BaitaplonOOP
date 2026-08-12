package com.expensemanager.controller;

import com.expensemanager.model.category.Category;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller điều khiển màn hình Danh mục.
 */
public class CategoryController {

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, String> nameColumn;

    @FXML
    private TableColumn<Category, String> descriptionColumn;

    @FXML
    private TableColumn<Category, Void> actionColumn;

    @FXML
    private Label categoryCountLabel;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    /**
     * JavaFX tự gọi sau khi load category.fxml.
     */
    @FXML
    private void initialize() {

        categoryTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        /*
         * Tên danh mục.
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
         * Mô tả.
         */
        descriptionColumn.setCellValueFactory(
                cellData -> {

                    String description =
                            cellData
                                    .getValue()
                                    .getDescription();

                    return new SimpleStringProperty(
                            description == null
                                    ? ""
                                    : description
                    );
                }
        );

        /*
         * Nút Sửa / Xóa.
         */
        setupActionColumn();

        /*
         * Load danh sách lần đầu.
         */
        refreshCategoryTable();
    }

    /**
     * Tạo nút Sửa / Xóa cho từng dòng.
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
                                /*
                                 * Sửa.
                                 */
                                editButton.setOnAction(
                                        event -> {

                                            Category category =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            handleEditCategory(
                                                    category
                                            );
                                        }
                                );

                                /*
                                 * Xóa.
                                 */
                                deleteButton.setOnAction(
                                        event -> {

                                            Category category =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            handleDeleteCategory(
                                                    category
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
     * Load / refresh danh sách Category.
     */
    private void refreshCategoryTable() {

        List<Category> categories =
                expenseManager.getCategories();

        ObservableList<Category> observableCategories =
                FXCollections.observableArrayList(
                        categories
                );

        categoryTable.setItems(
                observableCategories
        );

        categoryCountLabel.setText(
                observableCategories.size()
                        + " danh mục"
        );

        categoryTable.refresh();
    }

    /**
     * Mở form thêm Category.
     */
    @FXML
    private void handleOpenCategoryForm() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/com/expensemanager/view/category-form.fxml"
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
                    "Thêm danh mục"
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
             * Refresh sau khi popup đóng.
             */
            refreshCategoryTable();

        } catch (IOException e) {

            System.out.println(
                    "Không thể mở form thêm danh mục."
            );

            e.printStackTrace();
        }
    }

    /**
     * Mở form sửa Category.
     */
    private void handleEditCategory(
            Category category
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/com/expensemanager/view/category-form.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            CategoryFormController formController =
                    loader.getController();

            /*
             * Truyền Category đang sửa sang form.
             */
            formController.setEditingCategory(
                    category
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
                    "Sửa danh mục"
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
             * Refresh sau khi sửa.
             */
            refreshCategoryTable();

        } catch (IOException e) {

            System.out.println(
                    "Không thể mở form sửa danh mục."
            );

            e.printStackTrace();
        }
    }

    /**
     * Xóa Category.
     */
    private void handleDeleteCategory(
            Category category
    ) {

        Alert confirmAlert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmAlert.setTitle(
                "Xác nhận xóa"
        );

        confirmAlert.setHeaderText(
                "Bạn có chắc muốn xóa danh mục này?"
        );

        confirmAlert.setContentText(
                category.getName()
        );

        Optional<ButtonType> result =
                confirmAlert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try {

                expenseManager.removeCategory(
                        category
                );

                refreshCategoryTable();

            } catch (Exception e) {

                String message =
                        e.getMessage();

                if (message == null
                        || message.isBlank()) {

                    message =
                            "Không thể xóa danh mục.";
                }

                Alert errorAlert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                errorAlert.setTitle(
                        "Không thể xóa danh mục"
                );

                errorAlert.setHeaderText(
                        "Xóa danh mục thất bại"
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
