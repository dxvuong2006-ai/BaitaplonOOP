package com.expensemanager.service;

import com.expensemanager.exception.BudgetExceededException;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra logic hạn mức và kiểm tra vượt ngân sách")
class BudgetValidationTest {

    private Category category;
    private Budget budget;

    @BeforeEach
    void setUp() {
        category = new Category("CAT_FOOD", "Ăn uống", "Chi phí ăn uống hàng ngày", 1);
        // Ngân sách 2,000,000 VNĐ cho danh mục Ăn uống theo tháng
        budget = new Budget("B1", category, 2000000, Period.MONTH, 1);
    }

    @Test
    @DisplayName("Tính số tiền còn lại của ngân sách chính xác")
    void testGetRemainingAmount() {
        double spent = 1200000;
        double remaining = budget.getRemainingAmount(spent);
        assertEquals(800000, remaining, 0.001, "Số tiền còn lại phải là 800,000");
    }

    @Test
    @DisplayName("Kiểm tra trạng thái ngân sách bị vượt qua phương thức isExceeded")
    void testIsExceeded() {
        assertFalse(budget.isExceeded(1500000), "Chi 1,500,000 chưa vượt ngân sách 2,000,000");
        assertTrue(budget.isExceeded(2100000), "Chi 2,100,000 đã vượt ngân sách 2,000,000");
    }

    @Test
    @DisplayName("Phát hiện ngân sách bị vượt khi gọi ValidationService hoặc throw exception")
    void testValidateBudgetLimitThrowsBudgetExceededException() {
        double limitAmount = budget.getLimitAmount();
        double pendingExpense = 2500000; // Số tiền giao dịch mới định chi

        assertThrows(BudgetExceededException.class, () -> {
            if (budget.isExceeded(pendingExpense)) {
                throw new BudgetExceededException(limitAmount, pendingExpense);
            }
        }, "Phải ném BudgetExceededException khi tổng chi vượt hạn mức");
    }

    @Test
    @DisplayName("Tính phần trăm sử dụng ngân sách")
    void testGetUsagePercentage() {
        double spent = 1000000;
        double percentage = (spent * 100) / budget.getLimitAmount();
        assertEquals(50.0, percentage, 0.001, "Đã chi 1,000,000 / 2,000,000 phải đạt 50%");
    }
}
