package com.expensemanager.model;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private Wallet testWallet;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        testWallet = new CashWallet("W01", "Ví Test", 1000000);
        testCategory = new Category("C01", "Ăn uống","Ngon");
    }

    @Test
    public void testCreateExpense_ValidData_Success() {
        // Tạo giao dịch chi tiêu hợp lệ
        Expense expense = new Expense("T01", 50000, LocalDate.now(), "Ăn sáng", testCategory, testWallet, "Tiền mặt");

        assertNotNull(expense);
        assertEquals(50000, expense.getAmount());
        assertEquals("Ăn sáng", expense.getNote());
    }

    @Test
    public void testSetAmount_Negative_ShouldThrowException() {
        Expense expense = new Expense("T02", 50000, LocalDate.now(), "Test", testCategory, testWallet, "Tiền mặt");

        // Cố tình sửa số tiền thành âm
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            expense.setAmount(-20000);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("số tiền"));
    }

    @Test
    public void testSetCategory_Null_ShouldThrowException() {
        Expense expense = new Expense("T03", 50000, LocalDate.now(), "Test", testCategory, testWallet, "Tiền mặt");

        // Thử truyền null vào danh mục
        assertThrows(IllegalArgumentException.class, () -> {
            expense.setCategory(null);
        });
    }

    @Test
    public void testIncomeCreation_DoesNotRequirePaymentMethod() {
        // Đảm bảo Income tạo thành công với Source (Nguồn tiền) thay vì PaymentMethod
        Income income = new Income("T04", 5000000, LocalDate.now(), "Lương", testCategory, testWallet, "Công ty");
        assertEquals("Công ty", income.getSource());
    }
}