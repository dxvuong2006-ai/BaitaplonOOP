package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Lớp kiểm thử (Unit Test) cho ExpenseManager.
 * Sử dụng JUnit 5 và Mockito để cô lập nghiệp vụ.
 */
class ExpenseManagerTest {

    private ExpenseManager manager;

    @BeforeEach
    void setUp() {
        manager = ExpenseManager.getInstance();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Dọn dẹp biến static Singleton sau mỗi test case để tránh Test Pollution
        Field instanceField = ExpenseManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    // =========================================================================
    // TEST SINGLETON
    // =========================================================================

    @Test
    @DisplayName("Test Singleton: Đảm bảo chỉ có 1 instance duy nhất được tạo ra")
    void testSingletonInstance() {
        ExpenseManager anotherInstance = ExpenseManager.getInstance();
        assertSame(manager, anotherInstance, "Cả hai tham chiếu phải trỏ về cùng 1 vùng nhớ");
    }

    // =========================================================================
    // TEST QUẢN LÝ VÍ (WALLET)
    // =========================================================================

    @Test
    @DisplayName("Thêm ví thành công")
    void testAddWallet_Success() {
        Wallet mockWallet = mock(Wallet.class);
        when(mockWallet.getName()).thenReturn("Ví Tiền Mặt");

        manager.addWallet(mockWallet);

        assertEquals(1, manager.getWallets().size());
        assertTrue(manager.getWallets().contains(mockWallet));
    }

    @Test
    @DisplayName("Thêm ví thất bại: Trùng tên ví")
    void testAddWallet_DuplicateName() {
        Wallet mockWallet1 = mock(Wallet.class);
        when(mockWallet1.getName()).thenReturn("Ví Momo");
        Wallet mockWallet2 = mock(Wallet.class);
        when(mockWallet2.getName()).thenReturn("Ví Momo"); // Cố tình trùng tên

        manager.addWallet(mockWallet1);

        assertThrows(DuplicateEntityException.class, () -> manager.addWallet(mockWallet2),
                "Hệ thống phải ném lỗi DuplicateEntityException khi tạo trùng tên ví");
    }

    @Test
    @DisplayName("Thêm ví thất bại: Ví null")
    void testAddWallet_Null() {
        assertThrows(EmptyFieldException.class, () -> manager.addWallet(null));
    }

    @Test
    @DisplayName("Xóa ví thành công")
    void testRemoveWallet() {
        Wallet mockWallet = mock(Wallet.class);
        when(mockWallet.getName()).thenReturn("Ví Tạm");

        manager.addWallet(mockWallet);
        manager.removeWallet(mockWallet);

        assertTrue(manager.getWallets().isEmpty());
    }

    // =========================================================================
    // TEST QUẢN LÝ DANH MỤC (CATEGORY)
    // =========================================================================

    @Test
    @DisplayName("Thêm danh mục thành công")
    void testAddCategory_Success() {
        Category mockCategory = mock(Category.class);
        when(mockCategory.getName()).thenReturn("Ăn Uống");

        manager.addCategory(mockCategory);

        assertEquals(1, manager.getCategories().size());
    }

    @Test
    @DisplayName("Thêm danh mục thất bại: Trùng tên")
    void testAddCategory_Duplicate() {
        Category mockCategory = mock(Category.class);
        when(mockCategory.getName()).thenReturn("Lương");

        manager.addCategory(mockCategory);

        assertThrows(DuplicateEntityException.class, () -> manager.addCategory(mockCategory));
    }

    // =========================================================================
    // TEST QUẢN LÝ GIAO DỊCH (TRANSACTION)
    // =========================================================================

    @Test
    @DisplayName("Thêm khoản THU: Tiền phải được cộng vào ví")
    void testAddTransaction_Income() {
        // 1. Chuẩn bị Mock
        Wallet mockWallet = mock(Wallet.class);
        Transaction mockIncome = mock(Transaction.class);

        when(mockIncome.getWallet()).thenReturn(mockWallet);
        when(mockIncome.getSignedAmount()).thenReturn(500000.0); // Số dương = Thu nhập

        // 2. Thực thi
        manager.addTransaction(mockIncome);

        // 3. Kiểm chứng
        assertEquals(1, manager.getTransactions().size());
        verify(mockWallet, times(1)).deposit(500000.0); // Đảm bảo hàm nạp tiền của ví được gọi
    }

    @Test
    @DisplayName("Thêm khoản CHI: Tiền phải bị trừ khỏi ví")
    void testAddTransaction_Expense() {
        Wallet mockWallet = mock(Wallet.class);
        Transaction mockExpense = mock(Transaction.class);

        when(mockWallet.getBalance()).thenReturn(1_000_000.0);

        when(mockExpense.getWallet()).thenReturn(mockWallet);
        when(mockExpense.getSignedAmount()).thenReturn(-200000.0);

        manager.addTransaction(mockExpense);

        verify(mockWallet).withdraw(200000.0); // Đảm bảo hàm trừ tiền của ví được gọi với số dương
    }

    @Test
    @DisplayName("Xóa khoản CHI: Tiền phải được hoàn lại vào ví")
    void testRemoveTransaction_Expense() {
        Wallet mockWallet = mock(Wallet.class);
        Transaction mockExpense = mock(Transaction.class);

        when(mockWallet.getBalance()).thenReturn(1_000_000.0);

        when(mockExpense.getWallet()).thenReturn(mockWallet);
        when(mockExpense.getSignedAmount()).thenReturn(-100000.0);

        manager.addTransaction(mockExpense);
        manager.removeTransaction(mockExpense);

        verify(mockWallet).deposit(100000.0); // Hoàn lại tiền chi
    }

    @Test
    @DisplayName("Thêm giao dịch thất bại: Giao dịch hoặc Ví rỗng")
    void testAddTransaction_NullWallet() {
        Transaction mockTransaction = mock(Transaction.class);
        when(mockTransaction.getWallet()).thenReturn(null); // Ví bị null

        assertThrows(EmptyFieldException.class, () -> manager.addTransaction(mockTransaction));
        assertThrows(EmptyFieldException.class, () -> manager.addTransaction(null));
    }
}
