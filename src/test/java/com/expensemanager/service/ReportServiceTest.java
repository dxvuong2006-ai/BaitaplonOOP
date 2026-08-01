package com.expensemanager.service;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.report.ReportData;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private ReportService reportService;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
        transactions = new ArrayList<>();
    }

    @Test
    void testFillSummary() {
        Transaction income = mock(Transaction.class);
        Transaction expense = mock(Transaction.class);

        when(income.getType()).thenReturn(TransactionType.INCOME);
        when(income.getAmount()).thenReturn(1000.0);

        when(expense.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense.getAmount()).thenReturn(300.0);

        transactions.add(income);
        transactions.add(expense);

        ReportData report = new ReportData();
        reportService.fillSummary(report, transactions);

        assertEquals(1000.0, report.getTotalIncome());
        assertEquals(300.0, report.getTotalExpense());
        assertEquals(700.0, report.getNetSaving());
    }

    @Test
    void testFillCategoryStatistics() {
        Category category = mock(Category.class);

        Transaction income = mock(Transaction.class);
        Transaction expense = mock(Transaction.class);

        when(income.getType()).thenReturn(TransactionType.INCOME);
        when(income.getCategory()).thenReturn(category);
        when(income.getAmount()).thenReturn(500.0);

        when(expense.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense.getCategory()).thenReturn(category);
        when(expense.getAmount()).thenReturn(200.0);

        transactions.add(income);
        transactions.add(expense);

        ReportData report = new ReportData();
        reportService.fillCategoryStatistics(report, transactions);

        assertEquals(500.0, report.getIncomeByCategory().get(category));
        assertEquals(200.0, report.getExpenseByCategory().get(category));
    }

    @Test
    void testFillWalletStatistics() {
        Wallet wallet = mock(Wallet.class);

        Transaction income = mock(Transaction.class);
        Transaction expense = mock(Transaction.class);

        when(income.getType()).thenReturn(TransactionType.INCOME);
        when(income.getWallet()).thenReturn(wallet);
        when(income.getAmount()).thenReturn(1000.0);

        when(expense.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense.getWallet()).thenReturn(wallet);
        when(expense.getAmount()).thenReturn(400.0);

        transactions.add(income);
        transactions.add(expense);

        ReportData report = new ReportData();
        reportService.fillWalletStatistics(report, transactions);

        assertEquals(1000.0, report.getIncomeByWallet().get(wallet));
        assertEquals(400.0, report.getExpenseByWallet().get(wallet));
    }

    @Test
    void testFillMonthlyStatistics() {
        Transaction income = mock(Transaction.class);
        Transaction expense = mock(Transaction.class);

        when(income.getType()).thenReturn(TransactionType.INCOME);
        when(income.getAmount()).thenReturn(800.0);
        when(income.getDate()).thenReturn(LocalDate.of(2025, 7, 10));

        when(expense.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense.getAmount()).thenReturn(300.0);
        when(expense.getDate()).thenReturn(LocalDate.of(2025, 7, 15));

        transactions.add(income);
        transactions.add(expense);

        ReportData report = new ReportData();
        reportService.fillMonthlyStatistics(report, transactions);

        YearMonth month = YearMonth.of(2025, 7);

        assertEquals(800.0, report.getIncomeByMonth().get(month));
        assertEquals(300.0, report.getExpenseByMonth().get(month));
    }

    @Test
    void testFillTransactionStatistics() {
        transactions.add(mock(Transaction.class));
        transactions.add(mock(Transaction.class));

        ReportData report = new ReportData();
        reportService.fillTransactionStatistics(report, transactions);

        assertEquals(2, report.getTotalTransactions());
    }

    @Test
    void testFillSummary_EmptyTransactions() {
        ReportData report = new ReportData();

        reportService.fillSummary(report, new ArrayList<>());

        assertEquals(0, report.getTotalIncome());
        assertEquals(0, report.getTotalExpense());
        assertEquals(0, report.getNetSaving());
    }

    @Test
    void testFillTransactionStatistics_EmptyList() {
        ReportData report = new ReportData();

        reportService.fillTransactionStatistics(report, new ArrayList<>());

        assertEquals(0, report.getTotalTransactions());
    }
}
