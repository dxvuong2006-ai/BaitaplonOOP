package com.expensemanager.service;

import com.expensemanager.model.report.ReportData;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.transaction.Transaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/** Lớp quản lý báo cáo. */
public class ReportService{
    private final StatisticsService statsService;

    /** Khởi tạo. */
    public ReportService() {
        statsService = new StatisticsService();
    }

    /** Tạo bản báo cáo. */
    public ReportData createReport(LocalDate startDate, LocalDate endDate) {
        ValidationService.validateDateRange(startDate, endDate);
        List<Transaction> transactions = statsService
                .getTransactionsInPeriod(ExpenseManager.
                        getInstance().getTransactions(), startDate, endDate);
        ReportData report = new ReportData();
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        fillSummary(report, transactions);
        fillCategoryStatistics(report, transactions);
        fillWalletStatistics(report, transactions);
        fillMonthlyStatistics(report, transactions);
        fillTransactionStatistics(report, transactions);
        return report;
    }

    /** Điền thông tin tổng quan của báo cáo. */
    public void fillSummary(ReportData report, List<Transaction> transactions) {
        report.setTotalIncome(
                statsService.calculateTotalIncome(transactions));
        report.setTotalExpense(
                statsService.calculateTotalExpense(transactions));
        report.setNetSaving(
                statsService.calculateNetSaving(transactions));
    }

    /** Điền thống kê theo danh mục. */
    public void fillCategoryStatistics(ReportData report, List<Transaction> transactions) {
        report.setIncomeByCategory(
                statsService.calculateIncomeByCategory(transactions));
        report.setExpenseByCategory(
                statsService.calculateExpenseByCategory(transactions));
    }

    /** Điền thống kê theo ví. */
    public void fillWalletStatistics(ReportData report, List<Transaction> transactions) {
        report.setIncomeByWallet(
                statsService.calculateIncomeByWallet(transactions));
        report.setExpenseByWallet(
                statsService.calculateExpenseByWallet(transactions));
    }

    /** Điền thống kê theo tháng. */
    public void fillMonthlyStatistics(ReportData report, List<Transaction> transactions) {
        report.setIncomeByMonth(
                statsService.calculateIncomeByMonth(transactions));
        report.setExpenseByMonth(
                statsService.calculateExpenseByMonth(transactions));
    }

    /** Điền thống kê các giao dịch. */
    public void fillTransactionStatistics(ReportData report, List<Transaction> transactions) {
        report.setTotalTransactions(
                statsService.countTransactions(transactions));
    }
}
