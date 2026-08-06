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

    /** Tạo bản báo cáo. */
    public ReportData createReport(LocalDate startDate, LocalDate endDate) {
        ValidationService.validateDateRange(startDate, endDate);
        List<Transaction> transactions = ExpenseManager.getInstance().getStatisticsService()
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
                ExpenseManager.getInstance().getStatisticsService().calculateTotalIncome(transactions));
        report.setTotalExpense(
                ExpenseManager.getInstance().getStatisticsService().calculateTotalExpense(transactions));
        report.setNetSaving(
                ExpenseManager.getInstance().getStatisticsService().calculateNetSaving(transactions));
    }

    /** Điền thống kê theo danh mục. */
    public void fillCategoryStatistics(ReportData report, List<Transaction> transactions) {
        report.setIncomeByCategory(
                ExpenseManager.getInstance().getStatisticsService().calculateIncomeByCategory(transactions));
        report.setExpenseByCategory(
                ExpenseManager.getInstance().getStatisticsService().calculateExpenseByCategory(transactions));
    }

    /** Điền thống kê theo ví. */
    public void fillWalletStatistics(ReportData report, List<Transaction> transactions) {
        report.setIncomeByWallet(
                ExpenseManager.getInstance().getStatisticsService().calculateIncomeByWallet(transactions));
        report.setExpenseByWallet(
                ExpenseManager.getInstance().getStatisticsService().calculateExpenseByWallet(transactions));
    }

    /** Điền thống kê theo tháng. */
    public void fillMonthlyStatistics(ReportData report, List<Transaction> transactions) {
        report.setIncomeByMonth(
                ExpenseManager.getInstance().getStatisticsService().calculateIncomeByMonth(transactions));
        report.setExpenseByMonth(
                ExpenseManager.getInstance().getStatisticsService().calculateExpenseByMonth(transactions));
    }

    /** Điền thống kê các giao dịch. */
    public void fillTransactionStatistics(ReportData report, List<Transaction> transactions) {
        report.setTotalTransactions(
                ExpenseManager.getInstance().getStatisticsService().countTransactions(transactions));
    }
}
