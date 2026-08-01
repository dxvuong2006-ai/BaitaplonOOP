package com.expensemanager.service;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    private StatisticsService statisticsService;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsService();
        transactions = new ArrayList<>();
    }

    @Test
    void testGetTransactionsInPeriod() {
        Transaction t1 = mock(Transaction.class);
        Transaction t2 = mock(Transaction.class);

        when(t1.getDate()).thenReturn(LocalDate.of(2025, 7, 10));
        when(t2.getDate()).thenReturn(LocalDate.of(2025, 8, 10));

        transactions.add(t1);
        transactions.add(t2);

        List<Transaction> result = statisticsService.getTransactionsInPeriod(
                transactions,
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 31));

        assertEquals(1, result.size());
        assertTrue(result.contains(t1));
    }

    @Test
    void testCalculateTotalIncome() {
        Transaction income1 = mock(Transaction.class);
        Transaction income2 = mock(Transaction.class);

        when(income1.getType()).thenReturn(TransactionType.INCOME);
        when(income1.getAmount()).thenReturn(1000.0);

        when(income2.getType()).thenReturn(TransactionType.INCOME);
        when(income2.getAmount()).thenReturn(500.0);

        transactions.add(income1);
        transactions.add(income2);

        assertEquals(1500.0,
                statisticsService.calculateTotalIncome(transactions));
    }

    @Test
    void testCalculateTotalExpense() {
        Transaction expense1 = mock(Transaction.class);
        Transaction expense2 = mock(Transaction.class);

        when(expense1.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense1.getAmount()).thenReturn(800.0);

        when(expense2.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense2.getAmount()).thenReturn(200.0);

        transactions.add(expense1);
        transactions.add(expense2);

        assertEquals(1000.0,
                statisticsService.calculateTotalExpense(transactions));
    }

    @Test
    void testCalculateNetSaving() {
        Transaction income = mock(Transaction.class);
        Transaction expense = mock(Transaction.class);

        when(income.getType()).thenReturn(TransactionType.INCOME);
        when(income.getAmount()).thenReturn(2000.0);

        when(expense.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense.getAmount()).thenReturn(500.0);

        transactions.add(income);
        transactions.add(expense);

        assertEquals(1500.0,
                statisticsService.calculateNetSaving(transactions));
    }

    @Test
    void testCountTransactions() {
        transactions.add(mock(Transaction.class));
        transactions.add(mock(Transaction.class));
        transactions.add(null);

        assertEquals(2,
                statisticsService.countTransactions(transactions));
    }
}
