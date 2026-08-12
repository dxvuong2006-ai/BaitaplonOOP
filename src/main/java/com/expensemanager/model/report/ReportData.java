package com.expensemanager.model.report;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;

import java.time.YearMonth;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/** Lớp xử lý thông tin cần báo cáo. */
public class ReportData {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalIncome;
    private double totalExpense;
    private double netSaving;

    private Map<Category, Double> incomeByCategory;
    private Map<Category, Double> expenseByCategory;
    private Map<Wallet, Double> incomeByWallet;
    private Map<Wallet, Double> expenseByWallet;
    private Map<YearMonth, Double> incomeByMonth;
    private Map<YearMonth, Double> expenseByMonth;

    private int totalTransactions;
    private int totalIncomeTransactions;
    private int totalExpenseTransactions;

    /** Khởi tạo. */
    public ReportData() {
        incomeByCategory = new HashMap<>();
        expenseByCategory = new HashMap<>();
        incomeByWallet = new HashMap<>();
        expenseByWallet = new HashMap<>();
        incomeByMonth = new HashMap<>();
        expenseByMonth = new HashMap<>();
    }

    /** Lấy ngày bắt đầu. */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** Thiệt lập ngày bắt đầu. */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** Lấy ngày kết thúc. */
    public LocalDate getEndDate() {
        return endDate;
    }

    /** Thiết lập ngày kết thúc. */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /** Tổng thu. */
    public double getTotalIncome() {
        return totalIncome;
    }

    /** Thiết lập tổng thu. */
    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    /** Tổng chi. */
    public double getTotalExpense() {
        return totalExpense;
    }

    /** Thiết lập tổng chi. */
    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    /** Chênh lệch giữa thu và chi. */
    public double getNetSaving() {
        return netSaving;
    }

    /** Thiết lập chênh lệch. */
    public void setNetSaving(double netSaving) {
        this.netSaving = netSaving;
    }

    /** Tổng thu theo loại. */
    public Map<Category, Double> getIncomeByCategory() {
        return incomeByCategory;
    }

    /** Thiết lập tổng thu theo loại. */
    public void setIncomeByCategory(Map<Category, Double> incomeByCategory) {
        this.incomeByCategory = incomeByCategory;
    }

    /** Lấy tổng chi theo danh mục. */
    public Map<Category, Double> getExpenseByCategory() {
        return expenseByCategory;
    }

    /** Thiết lập tổng chi theo danh mục. */
    public void setExpenseByCategory(Map<Category, Double> expenseByCategory) {
        this.expenseByCategory = expenseByCategory;
    }

    /** Lấy tổng thu theo ví. */
    public Map<Wallet, Double> getIncomeByWallet() {
        return incomeByWallet;
    }

    /** Thiết lập tổng thu theo ví. */
    public void setIncomeByWallet(Map<Wallet, Double> incomeByWallet) {
        this.incomeByWallet = incomeByWallet;
    }

    /** Lấy tổng chi theo ví. */
    public Map<Wallet, Double> getExpenseByWallet() {
        return expenseByWallet;
    }

    /** Thiết lập tổng chi theo ví. */
    public void setExpenseByWallet(Map<Wallet, Double> expenseByWallet) {
        this.expenseByWallet = expenseByWallet;
    }

    /** Lấy tổng thu theo tháng. */
    public Map<YearMonth, Double> getIncomeByMonth() {
        return incomeByMonth;
    }

    /** Thiết lập tổng thu theo tháng. */
    public void setIncomeByMonth(Map<YearMonth, Double> incomeByMonth) {
        this.incomeByMonth = incomeByMonth;
    }

    /** Lấy tổng chi theo tháng. */
    public Map<YearMonth, Double> getExpenseByMonth() {
        return expenseByMonth;
    }

    /** Thiết lập tổng chi theo tháng. */
    public void setExpenseByMonth(Map<YearMonth, Double> expenseByMonth) {
        this.expenseByMonth = expenseByMonth;
    }

    /** Lấy tổng số giao dịch. */
    public int getTotalTransactions() {
        return totalTransactions;
    }

    /** Thiết lập tổng số giao dịch. */
    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    /** Lấy số lượng giao dịch thu. */
    public int getTotalIncomeTransactions() {
        return totalIncomeTransactions;
    }

    /** Thiết lập số lượng giao dịch thu. */
    public void setTotalIncomeTransactions(int totalIncomeTransactions) {
        this.totalIncomeTransactions = totalIncomeTransactions;
    }

    /** Lấy số lượng giao dịch chi. */
    public int getTotalExpenseTransactions() {
        return totalExpenseTransactions;
    }

    /** Thiết lập số lượng giao dịch chi. */
    public void setTotalExpenseTransactions(int totalExpenseTransactions) {
        this.totalExpenseTransactions = totalExpenseTransactions;
    }
}
