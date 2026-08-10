package com.expensemanager.model.transaction;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.wallet.Wallet;
import java.time.LocalDate;

/** Class các khoản chi tiêu định kỳ. */
public class RecurringExpense extends Expense {
    private Period period;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public RecurringExpense() {
        super();
    }

    /** Khởi tạo một khoản chi tiêu định kỳ. */
    public RecurringExpense(String id, double amount, LocalDate date, String note,
                            Category category, Wallet wallet, String paymentMethod, Period period, int userId) {
        super(id, amount, date, note, category, wallet, paymentMethod, userId);
        setPeriod(period);
    }

    /** Lấy chu kỳ lặp lại. */
    public Period getPeriod() {
        return period;
    }

    /** Gán lại chu kỳ lặp lại. */
    public void setPeriod(Period period) {
        this.period = period;
    }

    /** Tính toán ngày đến hạn của chu kỳ tiếp theo. */
    public LocalDate nextDueDate() {
        switch (this.period) {
            case DAILY:
                return getDate().plusDays(1);
            case WEEKLY:
                return getDate().plusWeeks(1);
            case MONTH:
                return getDate().plusMonths(1);
            case YEARLY:
                return getDate().plusYears(1);
            default:
                throw new UnsupportedOperationException("Chu kỳ không được hỗ trợ tính toán.");
        }
    }
}