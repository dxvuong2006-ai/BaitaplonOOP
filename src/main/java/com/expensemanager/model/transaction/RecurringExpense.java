package com.expensemanager.model.transaction;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.wallet.Wallet;

import java.time.LocalDate;

/** Class các khoản chi tiêu định kỳ. */
public class RecurringExpense extends Expense {
    /** Chu kỳ lặp lại của khoản chi. */
    private Period period;
    /** Ngày đến hạn tiếp theo của khoản chi. */
    private LocalDate nextDueDate;
    /** Trạng thái hoạt động của lịch định kỳ. */
    private boolean active;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public RecurringExpense() {
        super();
        this.active = true;
    }

    /** Khởi tạo một khoản chi tiêu định kỳ. */
    public RecurringExpense(String id, double amount, LocalDate date, String note,
                            Category category, Wallet wallet, String paymentMethod, Period period, int userId) {
        super(id, amount, date, note, category, wallet, paymentMethod, userId);
        setPeriod(period);
        setNextDueDate(date);
        this.active = true;
    }

    /** .Reconstructor */
    public RecurringExpense(
            String id,
            double amount,
            LocalDate date,
            String note,
            Category category,
            Wallet wallet,
            String paymentMethod,
            Period period,
            int userId,
            LocalDate nextDueDate,
            boolean active
    ) {
        super(
                id,
                amount,
                date,
                note,
                category,
                wallet,
                paymentMethod,
                userId
        );
        setPeriod(period);
        setNextDueDate(nextDueDate != null ? nextDueDate : date);
        this.active = active;
    }

    /** Trả về loại giao dịch là RECURRING_EXPENSE. */
    @Override
    public TransactionType getType() {
        return TransactionType.RECURRING_EXPENSE;
    }

    /** Lấy chu kỳ lặp lại. */
    public Period getPeriod() {
        return period;
    }

    /** Gán lại chu kỳ lặp lại. */
    public void setPeriod(Period period) {
        if (period == null) {
            throw new EmptyFieldException(FieldType.PERIOD);
        }
        this.period = period;
    }

    /** Trả về ngày đến hạn tiếp theo. */
    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    /** Cập nhật ngày đến hạn tiếp theo. */
    public void setNextDueDate(LocalDate nextDueDate) {
        if (nextDueDate == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        this.nextDueDate = nextDueDate;
    }

    /** Trả về true nếu khoản chi định kỳ đang hoạt động. */
    public boolean isActive() {
        return active;
    }

    /** Cập nhật trạng thái hoạt động của khoản chi định kỳ. */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** Tắt khoản chi định kỳ. */
    public void deactivate() {
        this.active = false;
    }

    /** Bật khoản chi định kỳ. */
    public void activate() {
        this.active = true;
    }

    /** Tính ngày đến hạn tiếp theo dựa trên ngày được truyền vào. */
    public LocalDate calculateNextDueDate(LocalDate date) {
        if (date == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        if (period == null) {
            throw new EmptyFieldException(FieldType.PERIOD);
        }
        return switch (period) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
            case MONTH -> date.plusMonths(1);
            case YEARLY -> date.plusYears(1);
        };
    }

    /** Chuyển ngày đến hạn hiện tại sang kỳ tiếp theo. */
    public void moveToNextDueDate() {
        if (nextDueDate == null) {
            setNextDueDate(getDate());
        }
        nextDueDate = calculateNextDueDate(nextDueDate);
    }

    /** Kiểm tra khoản chi định kỳ đã đến hạn hay chưa. */
    public boolean isDue(LocalDate currentDate) {
        if (currentDate == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        return active && nextDueDate != null && !nextDueDate.isAfter(currentDate);
    }

    /** Tính số kỳ đã đến hạn tính từ nextDueDate đến currentDate. */
    public int getDueOccurrenceCount(LocalDate currentDate) {
        if (currentDate == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        if (!active || nextDueDate == null) {
            return 0;
        }
        int count = 0;
        LocalDate dueDate = nextDueDate;
        while (!dueDate.isAfter(currentDate)) {
            count++;
            dueDate = calculateNextDueDate(dueDate);
        }
        return count;
    }

    /** Trả về ngày bắt đầu của khoản chi định kỳ. */
    public LocalDate getStartDate() {
        return getDate();
    }

    @Override
    public String toString() {
        return "RecurringExpense{" +
                "id='" + getId() + '\'' +
                ", amount=" + getAmount() +
                ", startDate=" + getStartDate() +
                ", nextDueDate=" + nextDueDate +
                ", period=" + period +
                ", active=" + active +
                ", userId=" + getUserId() +
                '}';
    }
}
