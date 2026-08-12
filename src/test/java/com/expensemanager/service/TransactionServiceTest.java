package com.expensemanager.service;

import com.expensemanager.factory.storage.TransactionStorageFactory;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test cập nhật số dư ví thông qua TransactionService")
class TransactionServiceTest {

    private TransactionService transactionService;
    private WalletService walletService;
    private BudgetService budgetService;
    private CategoryService categoryService;
    private TransactionStorageFactory storageFactory;

    private Wallet wallet;
    private Category category;

    @BeforeEach
    void setUp() {
        walletService = mock(WalletService.class);
        budgetService = mock(BudgetService.class);
        categoryService = mock(CategoryService.class);
        storageFactory = mock(TransactionStorageFactory.class);

        when(storageFactory.load(any())).thenReturn(List.of());

        transactionService = new TransactionService(budgetService, walletService, categoryService, storageFactory);

        wallet = new CashWallet("W1", "Ví chính", 1000000.0, 1);
        category = new Category("C1", "Ăn uống", "Mô tả", 1);

        when(walletService.findWalletById("W1", 1)).thenReturn(wallet);
    }

    @Test
    @DisplayName("Thêm giao dịch thu nhập (Income) làm tăng số dư ví")
    void testAddIncomeTransactionIncreasesWalletBalance() {
        Income income = new Income("T1", 500000, LocalDate.now(), "Thưởng", category, wallet, "Sếp", 1);

        transactionService.addTransaction(income, 1);

        assertEquals(1500000, wallet.getBalance(), 0.001, "Số dư ví phải tăng thêm 500,000");
        verify(walletService, times(1)).save();
    }

    @Test
    @DisplayName("Xóa giao dịch chi tiêu (Expense) hoàn lại tiền vào ví")
    void testRemoveExpenseRestoresWalletBalance() {
        Expense expense = new Expense("T2", 200000, LocalDate.now(), "Mua đồ", category, wallet, "Tiền mặt", 1);

        // Giả lập giao dịch đã tồn tại trong danh sách
        transactionService.addTransaction(expense, 1); // Balance còn 800,000

        transactionService.removeTransaction(expense, 1); // Hoàn lại 200,000

        assertEquals(1000000, wallet.getBalance(), 0.001, "Xóa giao dịch chi tiêu phải hoàn tiền về 1,000,000");
    }
}
