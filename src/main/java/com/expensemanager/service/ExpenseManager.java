package com.expensemanager.service;

import com.expensemanager.model.enums.ReportType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.user.User;
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
import com.expensemanager.service.UserService;
import com.expensemanager.model.report.ReportData;
import com.expensemanager.factory.storage.WalletStorageFactory;
import com.expensemanager.factory.storage.CategoryStorageFactory;
import com.expensemanager.factory.storage.BudgetStorageFactory;
import com.expensemanager.factory.storage.TransactionStorageFactory;
import com.expensemanager.factory.storage.UserStorageFactory;

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

    private final UserStorageFactory userStorageFactory;
    private final WalletStorageFactory walletStorageFactory;
    private final CategoryStorageFactory categoryStorageFactory;
    private final BudgetStorageFactory budgetStorageFactory;
    private final TransactionStorageFactory transactionStorageFactory;

    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final StatisticsService statisticsService;
    private final ReportService reportService;

    /** Phương thức khởi tạo của EM. */
    private ExpenseManager() {
        StorageType storageType = StorageType.JSON;

        userStorageFactory = new UserStorageFactory(storageType);
        walletStorageFactory = new WalletStorageFactory(storageType);
        categoryStorageFactory = new CategoryStorageFactory(storageType);
        budgetStorageFactory = new BudgetStorageFactory(storageType);
        transactionStorageFactory = new TransactionStorageFactory(storageType);

        userService = new UserService(userStorageFactory);
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

    public UserService getUserService() {
        return userService;
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

    //Tài khoản.
    /** Đăng ký tài khoản. */
    public User register(String id, String username, String rawPassword, String email) {
        return userService.register(id, username, rawPassword, email);
    }

    /** Đăng nhập. */
    public User login(String username, String rawPassword) {
        return userService.login(username, rawPassword);
    }

    /** Đăng xuất. */
    public void logout() {
        userService.logout();
    }

    /** Người dùng hiện tại. */
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    /** ID người dùng hiện tại. */
    public int getCurrentUserId() {
        return userService.getCurrentUserId();
    }

    /** Tìm tài khoản bằng tên. */
    public User findByUsername(String username) {
        return userService.findByUsername(username);
    }

    /** Tìm tài khoản bằng ID. */
    public User findById(String id) {
        return userService.findById(id);
    }

    /** Trả về danh sách tài khoản. */
    public List<User> getUser() {
        return userService.getUsers();
    }

    // Giao dịch.
    /** Thêm giao dịch. */
    public void addTransaction(Transaction transaction) {
        int userId = getCurrentUserId();
        transactionService.addTransaction(transaction, userId);
    }

    /** Xóa giao dịch. */
    public void removeTransaction(Transaction transaction) {
        int userId = getCurrentUserId();
        transactionService.removeTransaction(transaction, userId);
    }

    /** Chỉnh sửa giao dịch. */
    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        int userId = getCurrentUserId();
        transactionService.updateTransaction(oldTransaction, newTransaction, userId);
    }

    /** Trả về danh sách giao dịch. */
    public List<Transaction> getTransactions() {
        int userId = getCurrentUserId();
        return transactionService.getTransactions(userId);
    }

    /** Tìm giao dịch bằng ID. */
    public Transaction findTransactionById(String id) {
        int userId = getCurrentUserId();
        return transactionService.findTransactionById(id, userId);
    }

    /** Tìm giao dịch bằng ví. */
    public List<Transaction> findTransactionByWallet(Wallet wallet) {
        int userId = getCurrentUserId();
        return transactionService.findTransactionByWallet(wallet, userId);
    }

    /** Tìm giao dịch bằng danh mục. */
    public List<Transaction> findTransactionByCategory(Category category) {
        int userId = getCurrentUserId();
        return transactionService.findTransactionByCategory(category, userId);
    }

    /** Tìm giao dịch theo loại. */
    public List<Transaction> findTransactionByType(TransactionType type) {
        int userId = getCurrentUserId();
        return transactionService.findTransactionByType(type, userId);
    }

    // Ví.
    /** Thêm ví. */
    public void addWallet(Wallet wallet) {
        int userId = getCurrentUserId();
        walletService.addWallet(wallet, userId);
    }

    /** Xóa ví. */
    public void removeWallet(Wallet wallet) {
        int userId = getCurrentUserId();
        walletService.removeWallet(wallet, userId);
    }

    /** Chỉnh sửa ví. */
    public void updateWallet(Wallet oldwallet, Wallet newWallet) {
        int userId = getCurrentUserId();
        walletService.updateWallet(oldwallet, newWallet, userId);
    }

    /** Tìm ví theo ID. */
    public Wallet findWalletById(String id) {
        int userId = getCurrentUserId();
        return walletService.findWalletById(id, userId);
    }

    /** Tìm ví theo tên */
    public Wallet findWalletByName(String name) {
        int userId = getCurrentUserId();
        return walletService.findWalletByName(name, userId);
    }

    /** Trả về danh sách ví. */
    public List<Wallet> getWallets() {
        int userId = getCurrentUserId();
        return walletService.getWallets(userId);
    }

    // Danh mục.
    /** Thêm danh mục. */
    public void addCategory(Category category) {
        int userId = getCurrentUserId();
        categoryService.addCategory(category, userId);
    }

    /** Xóa danh mục. */
    public void removeCategory(Category category) {
        int userId = getCurrentUserId();
        categoryService.removeCategory(category, userId);
    }

    /** Cập nhật danh mục. */
    public void updateCategory(Category oldCategory, Category newCategory) {
        int userId = getCurrentUserId();
        categoryService.updateCategory(oldCategory, newCategory, userId);
    }

    /** Tìm danh mục bằng ID. */
    public Category findCategoryById(String id) {
        int userId = getCurrentUserId();
        return categoryService.findCategoryById(id, userId);
    }

    /** Tìm kiếm danh mục bằng tên. */
    public Category findCategoryByName(String name) {
        int userId = getCurrentUserId();
        return categoryService.findCategoryByName(name, userId);
    }

    /** Trả về danh sách danh mục. */
    public List<Category> getCategories() {
        int userId = getCurrentUserId();
        return categoryService.getCategories(userId);
    }

    // Ngân sách.
    /** Thêm ngân sách. */
    public void addBudget(Budget budget) {
        int userId = getCurrentUserId();
        budgetService.addBudget(budget, userId);
    }

    /** Xóa ngân sách. */
    public void removeBudget(Budget budget) {
        int userId = getCurrentUserId();
        budgetService.removeBudget(budget, userId);
    }

    /** Chỉnh sửa ngân sách. */
    public void updateBudget(Budget oldBudget, Budget newBudget) {
        int userId = getCurrentUserId();
        budgetService.updateBudget(oldBudget, newBudget, userId);
    }

    /** Tìm ngân sách bằng ID. */
    public Budget findBudgetById(String id) {
        int userId = getCurrentUserId();
        return budgetService.findBudgetById(id, userId);
    }

    /** Tìm kiếm ngân sách bằng danh mục. */
    public Budget findBudgetByName(Category category) {
        int userId = getCurrentUserId();
        return budgetService.findBudgetByCategory(category, userId);
    }

    /** Trả về danh sách ngân sách. */
    public List<Budget> getBudgets() {
        int userId = getCurrentUserId();
        return budgetService.getBudgets(userId);
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
