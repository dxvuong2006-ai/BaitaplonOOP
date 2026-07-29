package com.expensemanager.model.transaction;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.exception.EmptyFieldException;
import java.time.LocalDate;

/** Class cac khoan chi tieu dinh ky. */
public class RecurringExpense extends Expense{
    private Period period;

    public RecurringExpense() {
        super();
    }

    public RecurringExpense(String id, double amount, LocalDate date,String note,Category category, Wallet wallet, String paymentMethod, Period period) {
        super(id, amount, date, note, category,wallet, paymentMethod);
        setPeriod(period);
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        if (period == null) {
            throw new EmptyFieldException(FieldType.PERIOD);
        }
        this.period = period;
    }

    /** Tinh toan ngay den han cua chu ky tiep. */
    public LocalDate nextDueDate() {
        if (getDate() == null || this.period == null) {
            return null;
        }
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
