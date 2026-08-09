package com.expensemanager.controller;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.service.ExpenseManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controller điều khiển Dashboard.
 *
 * Chỉ lấy dữ liệu thông qua ExpenseManager
 * và các service backend hiện có.
 */
public class DashboardController {

    @FXML
    private Label currentPeriodLabel;

    @FXML
    private Label totalBalanceLabel;

    @FXML
    private Label totalIncomeLabel;

    @FXML
    private Label totalExpenseLabel;

    @FXML
    private Label netSavingLabel;

    @FXML
    private Label transactionCountLabel;

    @FXML
    private BarChart<String, Number> incomeExpenseChart;

    @FXML
    private PieChart expenseCategoryChart;

    @FXML
    private TableView<Transaction> recentTransactionTable;

    @FXML
    private TableColumn<Transaction, String> recentDateColumn;

    @FXML
    private TableColumn<Transaction, String> recentNoteColumn;

    @FXML
    private TableColumn<Transaction, String> recentWalletColumn;

    @FXML
    private TableColumn<Transaction, String> recentCategoryColumn;

    @FXML
    private TableColumn<Transaction, String> recentTypeColumn;

    @FXML
    private TableColumn<Transaction, String> recentAmountColumn;

    private final ExpenseManager expenseManager =
            ExpenseManager.getInstance();

    /**
     * JavaFX tự gọi sau khi load dashboard.fxml.
     */
    @FXML
    private void initialize() {

        setupRecentTransactionTable();

        refreshDashboard();
    }

    /**
     * Load toàn bộ dữ liệu Dashboard.
     */
    private void refreshDashboard() {

        updateCurrentPeriod();

        updateSummaryCards();

        updateIncomeExpenseChart();

        updateExpenseCategoryChart();

        updateRecentTransactions();
    }

    /**
     * Hiển thị tháng hiện tại.
     */
    private void updateCurrentPeriod() {

        LocalDate today =
                LocalDate.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "MM/yyyy"
                );

        currentPeriodLabel.setText(
                "Tháng "
                        + today.format(formatter)
        );
    }

    /**
     * Cập nhật các thẻ tổng quan.
     */
    private void updateSummaryCards() {

        /*
         * ==========================
         * TỔNG SỐ DƯ
         * ==========================
         */

        double totalBalance = 0.0;

        for (Wallet wallet :
                expenseManager.getWallets()) {

            if (wallet != null) {

                totalBalance +=
                        wallet.getBalance();
            }
        }

        totalBalanceLabel.setText(
                formatMoney(
                        totalBalance
                )
        );

        /*
         * ==========================
         * GIAO DỊCH TRONG THÁNG
         * ==========================
         */

        YearMonth currentMonth =
                YearMonth.now();

        LocalDate startDate =
                currentMonth.atDay(1);

        LocalDate endDate =
                currentMonth.atEndOfMonth();

        List<Transaction> allTransactions =
                expenseManager.getTransactions();

        List<Transaction> monthlyTransactions =
                expenseManager
                        .getTransactionsInPeriod(
                                allTransactions,
                                startDate,
                                endDate
                        );

        /*
         * Tổng thu.
         */
        double totalIncome =
                expenseManager
                        .calculateTotalIncome(
                                monthlyTransactions
                        );

        /*
         * Tổng chi.
         */
        double totalExpense =
                expenseManager
                        .calculateTotalExpense(
                                monthlyTransactions
                        );

        /*
         * Tiết kiệm ròng.
         */
        double netSaving =
                expenseManager
                        .calculateNetSaving(
                                monthlyTransactions
                        );

        /*
         * Số giao dịch.
         */
        int transactionCount =
                expenseManager
                        .countTransactions(
                                monthlyTransactions
                        );

        totalIncomeLabel.setText(
                formatMoney(
                        totalIncome
                )
        );

        totalExpenseLabel.setText(
                formatMoney(
                        totalExpense
                )
        );

        netSavingLabel.setText(
                formatMoney(
                        netSaving
                )
        );

        transactionCountLabel.setText(
                transactionCount
                        + " giao dịch trong tháng"
        );
    }

    /**
     * Biểu đồ thu / chi 6 tháng gần nhất.
     */
    private void updateIncomeExpenseChart() {

        incomeExpenseChart
                .getData()
                .clear();

        List<Transaction> transactions =
                expenseManager
                        .getTransactions();

        Map<YearMonth, Double> incomeByMonth =
                expenseManager
                        .calculateIncomeByMonth(
                                transactions
                        );

        Map<YearMonth, Double> expenseByMonth =
                expenseManager
                        .calculateExpenseByMonth(
                                transactions
                        );

        XYChart.Series<String, Number> incomeSeries =
                new XYChart.Series<>();

        incomeSeries.setName(
                "Thu"
        );

        XYChart.Series<String, Number> expenseSeries =
                new XYChart.Series<>();

        expenseSeries.setName(
                "Chi"
        );

        YearMonth currentMonth =
                YearMonth.now();

        /*
         * Hiển thị từ tháng cũ nhất
         * đến tháng hiện tại.
         */
        for (int i = 5; i >= 0; i--) {

            YearMonth month =
                    currentMonth.minusMonths(i);

            String monthLabel =
                    String.format(
                            "%02d/%d",
                            month.getMonthValue(),
                            month.getYear()
                    );

            double income =
                    incomeByMonth.getOrDefault(
                            month,
                            0.0
                    );

            double expense =
                    expenseByMonth.getOrDefault(
                            month,
                            0.0
                    );

            incomeSeries
                    .getData()
                    .add(
                            new XYChart.Data<>(
                                    monthLabel,
                                    income
                            )
                    );

            expenseSeries
                    .getData()
                    .add(
                            new XYChart.Data<>(
                                    monthLabel,
                                    expense
                            )
                    );
        }

        incomeExpenseChart
                .getData()
                .add(incomeSeries);

        incomeExpenseChart
                .getData()
                .add(expenseSeries);
    }

    /**
     * Biểu đồ chi tiêu theo danh mục
     * trong tháng hiện tại.
     */
    private void updateExpenseCategoryChart() {

        expenseCategoryChart
                .getData()
                .clear();

        YearMonth currentMonth =
                YearMonth.now();

        LocalDate startDate =
                currentMonth.atDay(1);

        LocalDate endDate =
                currentMonth.atEndOfMonth();

        List<Transaction> monthlyTransactions =
                expenseManager
                        .getTransactionsInPeriod(
                                expenseManager
                                        .getTransactions(),
                                startDate,
                                endDate
                        );

        Map<Category, Double> expenseByCategory =
                expenseManager
                        .calculateExpenseByCategory(
                                monthlyTransactions
                        );

        for (Map.Entry<Category, Double> entry :
                expenseByCategory.entrySet()) {

            Category category =
                    entry.getKey();

            Double amount =
                    entry.getValue();

            String categoryName;

            if (category == null) {

                categoryName =
                        "Không có danh mục";

            } else {

                categoryName =
                        category.getName();
            }

            expenseCategoryChart
                    .getData()
                    .add(
                            new PieChart.Data(
                                    categoryName,
                                    amount
                            )
                    );
        }
    }

    /**
     * Setup bảng giao dịch gần đây.
     */
    private void setupRecentTransactionTable() {

        recentDateColumn.setCellValueFactory(
                cellData -> {

                    LocalDate date =
                            cellData
                                    .getValue()
                                    .getDate();

                    return new SimpleStringProperty(
                            date == null
                                    ? ""
                                    : date.toString()
                    );
                }
        );

        recentNoteColumn.setCellValueFactory(
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

        recentWalletColumn.setCellValueFactory(
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

        recentCategoryColumn.setCellValueFactory(
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

        recentTypeColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getType()
                                        .toString()
                        )
        );

        recentAmountColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                formatMoney(
                                        cellData
                                                .getValue()
                                                .getAmount()
                                )
                        )
        );
    }

    /**
     * Hiển thị 5 giao dịch mới nhất.
     */
    private void updateRecentTransactions() {

        List<Transaction> transactions =
                new ArrayList<>(
                        expenseManager
                                .getTransactions()
                );

        /*
         * Sắp xếp ngày mới nhất lên trước.
         */
        transactions.sort(
                Comparator.comparing(
                                Transaction::getDate,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()
                                )
                        )
                        .reversed()
        );

        /*
         * Chỉ lấy tối đa 5 giao dịch.
         */
        if (transactions.size() > 5) {

            transactions =
                    new ArrayList<>(
                            transactions.subList(
                                    0,
                                    5
                            )
                    );
        }

        recentTransactionTable.setItems(
                FXCollections.observableArrayList(
                        transactions
                )
        );

        recentTransactionTable.refresh();
    }

    /**
     * Format tiền VND.
     */
    private String formatMoney(
            double amount
    ) {

        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale(
                                "vi",
                                "VN"
                        )
                );

        formatter.setMaximumFractionDigits(
                0
        );

        return formatter.format(
                amount
        ) + " đ";
    }
}