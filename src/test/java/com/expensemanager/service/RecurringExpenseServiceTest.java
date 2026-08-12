package com.expensemanager.service;

import com.expensemanager.factory.storage.RecurringExecutionStorageFactory;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.RecurringExecutionStatus;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.transaction.RecurringExecution;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test xử lý khoản chi định kỳ tự động")
class RecurringExpenseServiceTest {

    private TransactionService transactionService;
    private WalletService walletService;
    private RecurringExecutionStorageFactory storageFactory;
    private RecurringExpenseService recurringService;

    private Wallet wallet;
    private Category category;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        walletService = mock(WalletService.class);
        storageFactory = mock(RecurringExecutionStorageFactory.class);

        when(storageFactory.load(any())).thenReturn(List.of());

        recurringService = new RecurringExpenseService(storageFactory, transactionService, walletService);

        category = new Category("C1", "Tiền nhà", "Thuê nhà", 1);
        wallet = new CashWallet("W1", "Ví chính", 5000000.0, 1);
    }

    @Test
    @DisplayName("Ghi nhận FAILED khi số dư ví không đủ để thanh toán định kỳ")
    void testProcessDueExpenseFailedWhenInsufficientBalance() {
        // Ví chỉ có 500,000 nhưng khoản chi định kỳ là 1,000,000
        Wallet poorWallet = new CashWallet("W2", "Ví phụ", 500000.0, 1);
        RecurringExpense dueExpense = new RecurringExpense(
                "RE1", 1000000, LocalDate.now().minusDays(1), "Tiền nhà",
                category, poorWallet, "Chuyển khoản", Period.MONTH, 1, LocalDate.now().minusDays(1), true
        );

        when(transactionService.getTransactions(1)).thenReturn(List.of(dueExpense));

        recurringService.processDueExpenses(1);

        List<RecurringExecution> executions = recurringService.getExecutions("RE1", 1);
        assertFalse(executions.isEmpty());
        assertEquals(RecurringExecutionStatus.FAILED, executions.get(0).getStatus(),
                "Phải ghi nhận trạng thái FAILED do ví không đủ tiền");

        // Không tạo giao dịch chi tiêu mới
        verify(transactionService, never()).addTransaction(any(), eq(1));
    }
}
