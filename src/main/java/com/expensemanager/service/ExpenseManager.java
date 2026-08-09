package com.expensemanager.service;

import com.expensemanager.model.enums.ReportType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.service.BudgetService;
import com.expensemanager.service.CategoryService;
import com.expensemanager.service.WalletService;
import com.expensemanager.service.TransactionService;
import com.expensemanager.service.StatisticsService;
import com.expensemanager.model.report.ReportData;
import com.expensemanager.factory.storage.WalletStorageFactory;
import com.expensemanager.factory.storage.CategoryStorageFactory;
import com.expensemanager.factory.storage.BudgetStorageFactory;
import com.expensemanager.factory.storage.TransactionStorageFactory;

import com.expensemanager.model.enums.StorageType;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.exception.InsufficientFundsException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/** Lớp xử lý nghiệp vụ của dự án. */
public class ExpenseManager {
    private static ExpenseManager instance;

    private final WalletStorageFactory walletStorageFactory;
    private final CategoryStorageFactory categoryStorageFactory;
    private final BudgetStorageFactory budgetStorageFactory;
    private final TransactionStorageFactory transactionStorageFactory;

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final StatisticsService statisticsService;
    private final ReportService reportService;

    /** Phương thức khởi tạo của EM. */
    private ExpenseManager() {
        StorageType storageType = StorageType.JSON;

        walletStorageFactory = new WalletStorageFactory(storageType);
        categoryStorageFactory = new CategoryStorageFactory(storageType);
        budgetStorageFactory = new BudgetStorageFactory(storageType);
        transactionStorageFactory = new TransactionStorageFactory(storageType);

        walletService = new WalletService(walletStorageFactory);
        categoryService = new CategoryService(categoryStorageFactory);
        budgetService = new BudgetService(budgetStorageFactory, categoryService);
        transactionService = new TransactionService(budgetService, walletService,
                categoryService, transactionStorageFactory);

        statisticsService = new StatisticsService();
        reportService = new ReportService();
    }

    /** Giúp lấy object. */
    public static ExpenseManager getInstance() {
        if (instance == null) {
            instance = new ExpenseManager();
        }
        return instance;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public WalletService getWalletService() {
        return walletService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public BudgetService getBudgetService() {
        return budgetService;
    }

    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    // Giao dịch.
    /** Thêm giao dịch. */
    public void addTransaction(Transaction transaction) {
        transactionService.addTransaction(transaction);
    }

    /** Xóa giao dịch. */
    public void removeTransaction(Transaction transaction) {
        transactionService.removeTransaction(transaction);
    }

    /** Chỉnh sửa giao dịch. */
    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        transactionService.updateTransaction(oldTransaction, newTransaction);
    }

    /** Trả về danh sách giao dịch. */
    public List<Transaction> getTransactions() {
        return transactionService.getTransactions();
    }

    /** Tìm giao dịch bằng ID. */
    public Transaction findTransactionById(String id) {
        return transactionService.findTransactionById(id);
    }

    /** Tìm giao dịch bằng ví. */
    public List<Transaction> findTransactionByWallet(Wallet wallet) {
        return transactionService.findTransactionByWallet(wallet);
    }

    /** Tìm giao dịch bằng danh mục. */
    public List<Transaction> findTransactionByCategory(Category category) {
        return transactionService.findTransactionByCategory(category);
    }

    /** Tìm giao dịch theo loại. */
    public List<Transaction> findTransactionByType(TransactionType type) {
        return transactionService.findTransactionByType(type);
    }

    // Ví.
    /** Thêm ví. */
    public void addWallet(Wallet wallet) {
        walletService.addWallet(wallet);
    }

    /** Xóa ví. */
    public void removeWallet(Wallet wallet) {
        walletService.removeWallet(wallet);
    }

    /** Chỉnh sửa ví. */
    public void updateWallet(Wallet oldwallet, Wallet newWallet) {
        walletService.updateWallet(oldwallet, newWallet);
    }

    /** Tìm ví theo ID. */
    public Wallet findWalletById(String id) {
        return walletService.findWalletById(id);
    }

    /** Tìm ví theo tên */
    public Wallet findWalletByName(String name) {
        return walletService.findWalletByName(name);
    }

    /** Trả về danh sách ví. */
    public List<Wallet> getWallets() {
        return walletService.getWallets();
    }

    // Danh mục.
    /** Thêm danh mục. */
    public void addCategory(Category category) {
        categoryService.addCategory(category);
    }

    /** Xóa danh mục. */
    public void removeCategory(Category category) {
        categoryService.removeCategory(category);
    }

    /** Cập nhật danh mục. */
    public void updateCategory(Category oldCategory, Category newCategory) {
        categoryService.updateCategory(oldCategory, newCategory);
    }

    /** Tìm danh mục bằng ID. */
    public Category findCategoryById(String id) {
        return categoryService.findCategoryById(id);
    }

    /** Tìm kiếm danh mục bằng tên. */
    public Category findCategoryByName(String name) {
        return categoryService.findCategoryByName(name);
    }

    /** Trả về danh sách danh mục. */
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    // Ngân sách.
    /** Thêm ngân sách. */
    public void addBudget(Budget budget) {
        budgetService.addBudget(budget);
    }

    /** Xóa ngân sách. */
    public void removeBudget(Budget budget) {
        budgetService.removeBudget(budget);
    }

    /** Chỉnh sửa ngân sách. */
    public void updateBudget(Budget oldBudget, Budget newBudget) {
        budgetService.updateBudget(oldBudget, newBudget);
    }

    /** Tìm ngân sách bằng ID. */
    public Budget findBudgetById(String id) {
        return budgetService.findBudgetById(id);
    }

    /** Tìm kiếm ngân sách bằng danh mục. */
    public Budget findBudgetByName(Category category) {
        return budgetService.findBudgetByCategory(category);
    }

    /** Trả về danh sách ngân sách. */
    public List<Budget> getBudgets() {
        return budgetService.getBudgets();
    }

    /** Giúp hiển thị số tiền dạng chuẩn. */
    public String getFormattedBalance(Wallet wallet) {
        return CurrencyUtils.formatVND(wallet.getBalance());
    }

    // Liên kết ReportService.
    /** Tạo báo cáo. */
    public ReportData createReport(LocalDate startDate, LocalDate endDate) {
        return reportService.createReport(startDate, endDate);
    }

    /** Xuất báo cáo. */
    public File exportReport(LocalDate startDate, LocalDate endDate,
                             ReportType type, File outputFile) throws IOException {
        return reportService.exportReport(startDate, endDate, type, outputFile);
    }

    // Liên kết Statistic.
    /** . */
    public List<Transaction> getTransactionsInPeriod(List<Transaction> transactions,
                                                     LocalDate startDate, LocalDate endDate) {
        return statisticsService.getTransactionsInPeriod(transactions, startDate, endDate);
    }

    /** Tổng thu. */
    public double calculateTotalIncome(List<Transaction> transactions) {
        return statisticsService.calculateTotalIncome(transactions);
    }

    /** Tổng chi. */
    public double calculateTotalExpense(List<Transaction> transactions) {
        return statisticsService.calculateTotalExpense(transactions);
    }

    /** Chênh lệch giữa tổng thu và tổng chi. */
    public double calculateNetSaving(List<Transaction> transactions) {
        return statisticsService.calculateNetSaving(transactions);
    }

    /** Đếm số giao dịch. */
    public int countTransactions(List<Transaction> transactions) {
        return statisticsService.countTransactions(transactions);
    }

    /** Thống kê chi theo từng loại giao dịch. */
    public Map<Category, Double> calculateExpenseByCategory(List<Transaction> transactions) {
        return statisticsService.calculateExpenseByCategory(transactions);
    }

    /** Thống kê thu theo từng loại giao dịch. */
    public Map<Category, Double> calculateIncomeByCategory(List<Transaction> transactions) {
        return statisticsService.calculateIncomeByCategory(transactions);
    }

    /** Thống kê chi theo loại ví. */
    public Map<Wallet, Double> calculateExpenseByWallet(List<Transaction> transactions) {
        return statisticsService.calculateExpenseByWallet(transactions);
    }

    /** Thống kê thu theo loại ví. */
    public Map<Wallet, Double> calculateIncomeByWallet(List<Transaction> transactions) {
        return statisticsService.calculateIncomeByWallet(transactions);
    }

    /** Thống kê thu theo tháng. */
    public Map<YearMonth, Double> calculateIncomeByMonth(List<Transaction> transactions) {
        return statisticsService.calculateIncomeByMonth(transactions);
    }

    /** Thống kê chi theo tháng. */
    public Map<YearMonth, Double> calculateExpenseByMonth(List<Transaction> transactions) {
        return statisticsService.calculateExpenseByMonth(transactions);
    }
}
