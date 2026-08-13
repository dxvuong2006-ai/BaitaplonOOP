package com.expensemanager.service;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra logic tính tổng thu, chi và tiết kiệm ròng")
class StatisticsServiceTest {

    private StatisticsService statisticsService;
    private Category category;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsService();
        category = new Category("CAT1", "Ăn uống", "Mô tả", 1);
        wallet = new CashWallet("WAL1", "Ví chính", 1000000.0, 1);
    }

    @Test
    @DisplayName("Tính tổng khoản thu thành công từ danh sách giao dịch")
    void testCalculateTotalIncome() {
        List<Transaction> transactions = List.of(
                new Income("TX1", 500000, LocalDate.now(), "Lương", category, wallet, "Công ty", 1),
                new Income("TX2", 200000, LocalDate.now(), "Thưởng", category, wallet, "Dự án", 1),
                new Expense("TX3", 100000, LocalDate.now(), "Mua sắm", category, wallet, "Tiền mặt", 1)
        );

        double totalIncome = statisticsService.calculateTotalIncome(transactions);

        assertEquals(700000, totalIncome, 0.001, "Tổng thu phải là 700,000");
    }

    @Test
    @DisplayName("Tính tổng khoản chi thành công từ danh sách giao dịch")
    void testCalculateTotalExpense() {
        List<Transaction> transactions = List.of(
                new Income("TX1", 500000, LocalDate.now(), "Lương", category, wallet, "Công ty", 1),
                new Expense("TX2", 150000, LocalDate.now(), "Ăn trưa", category, wallet, "Tiền mặt", 1),
                new Expense("TX3", 50000, LocalDate.now(), "Cà phê", category, wallet, "Tiền mặt", 1)
        );

        double totalExpense = statisticsService.calculateTotalExpense(transactions);

        assertEquals(200000, totalExpense, 0.001, "Tổng chi phải là 200,000");
    }

    @Test
    @DisplayName("Tính tiết kiệm ròng (Tổng thu - Tổng chi) chính xác")
    void testCalculateNetSaving() {
        List<Transaction> transactions = List.of(
                new Income("TX1", 1000000, LocalDate.now(), "Lương", category, wallet, "Công ty", 1),
                new Expense("TX2", 300000, LocalDate.now(), "Mua sắm", category, wallet, "Thẻ", 1),
                new Expense("TX3", 200000, LocalDate.now(), "Đi lại", category, wallet, "Tiền mặt", 1)
        );

        double netSaving = statisticsService.calculateNetSaving(transactions);

        assertEquals(500000, netSaving, 0.001, "Tiết kiệm ròng phải là 500,000 (1,000,000 - 500,000)");
    }

    @Test
    @DisplayName("Tính tổng khi danh sách giao dịch rỗng trả về 0")
    void testCalculateWithEmptyList() {
        List<Transaction> transactions = new ArrayList<>();

        assertEquals(0, statisticsService.calculateTotalIncome(transactions));
        assertEquals(0, statisticsService.calculateTotalExpense(transactions));
        assertEquals(0, statisticsService.calculateNetSaving(transactions));
    }
}
