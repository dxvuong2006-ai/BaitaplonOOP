package com.expensemanager.service;

import com.expensemanager.exception.BudgetExceededException;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.wallet.CashWallet;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BudgetServiceTest {

    private ExpenseManager manager;
    private BudgetService budgetService;
    private Category category;
    private CashWallet wallet;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = ExpenseManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        manager = ExpenseManager.getInstance();
        budgetService = new BudgetService();

        category = new Category("C01", "Ăn uống", "Chi phí ăn uống");
        wallet = new CashWallet("W01", "Ví tiền mặt", 2_000_000);
    }

    @Test
    @DisplayName("Validate budget - not exceeded")
    void testValidateBudgetLimit_NotExceeded() {
        Expense expense = new Expense("E01", 200000, LocalDate.now(), "Ăn sáng",
                category, wallet, "Tiền mặt");
        manager.addTransaction(expense);

        Budget budget = new Budget("B01", category, 500000, Period.MONTH);

        assertDoesNotThrow(() -> budgetService.validateBudgetLimit(budget));
    }

    @Test
    @DisplayName("Validate budget - exceeded")
    void testValidateBudgetLimit_Exceeded() {
        Expense expense = new Expense("E01", 700000, LocalDate.now(), "Ăn uống",
                category, wallet, "Tiền mặt");
        manager.addTransaction(expense);

        Budget budget = new Budget("B01", category, 500000, Period.MONTH);

        assertThrows(BudgetExceededException.class,
                () -> budgetService.validateBudgetLimit(budget));
    }

    @Test
    @DisplayName("Get remaining budget")
    void testGetRemainingBudget() {
        Expense expense = new Expense("E01", 200000, LocalDate.now(), "Ăn trưa",
                category, wallet, "Tiền mặt");
        manager.addTransaction(expense);

        Budget budget = new Budget("B01", category, 500000, Period.MONTH);

        assertEquals(300000, budgetService.getRemainingBudget(budget));
    }

    @Test
    @DisplayName("Usage percentage")
    void testUsagePercentage() {
        Expense expense = new Expense("E01", 200000, LocalDate.now(), "Ăn trưa",
                category, wallet, "Tiền mặt");
        manager.addTransaction(expense);

        Budget budget = new Budget("B01", category, 500000, Period.MONTH);

        assertEquals(40.0, budgetService.getUsagePercentage(budget));
    }

    @Test
    @DisplayName("Usage percentage with null budget")
    void testUsagePercentage_NullBudget() {
        assertEquals(0, budgetService.getUsagePercentage(null));
    }

    @Test
    @DisplayName("Usage percentage with zero limit")
    void testUsagePercentage_ZeroLimit() {
        Budget budget = new Budget("B01", category, 0, Period.MONTH);

        assertEquals(0, budgetService.getUsagePercentage(budget));
    }

    @Test
    @DisplayName("Remaining budget without transactions")
    void testRemainingBudget_NoTransaction() {
        Budget budget = new Budget("B01", category, 1_000_000, Period.MONTH);

        assertEquals(1_000_000, budgetService.getRemainingBudget(budget));
    }
}
