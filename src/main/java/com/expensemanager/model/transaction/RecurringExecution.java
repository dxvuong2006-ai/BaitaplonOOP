package com.expensemanager.model.transaction;

import com.expensemanager.model.enums.RecurringExecutionStatus;
import java.time.LocalDate;

public class RecurringExecution {
    /** Mã định danh của lần thực thi. */
    private String id;
    /** Mã của khoản chi tiêu định kỳ. */
    private String recurringExpenseId;
    /** Mã người dùng sở hữu khoản chi tiêu định kỳ. */
    private int userId;
    /** Ngày đến hạn của kỳ được thực thi. */
    private LocalDate dueDate;
    /** Số tiền của kỳ được thực thi. */
    private double amount;
    /** Trạng thái thực thi của kỳ. */
    private RecurringExecutionStatus status;
    /** Mã giao dịch thực tế được tạo nếu thực thi thành công. */
    private String transactionId;
    /** Constructor mặc định, dùng cho quá trình deserialize. */
    public RecurringExecution() {
    }

    /** Khởi tạo một lần thực thi của khoản chi tiêu định kỳ. */
    public RecurringExecution(
            String id,
            String recurringExpenseId,
            int userId,
            LocalDate dueDate,
            double amount,
            RecurringExecutionStatus status,
            String transactionId
    ) {
        this.id = id;
        this.recurringExpenseId = recurringExpenseId;
        this.userId = userId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
    }

    /** Trả về mã của lần thực thi. */
    public String getId() {
        return id;
    }

    /** Cập nhật mã của lần thực thi. */
    public void setId(String id) {
        this.id = id;
    }

    /** Trả về mã khoản chi tiêu định kỳ. */
    public String getRecurringExpenseId() {
        return recurringExpenseId;
    }

    /** Cập nhật mã khoản chi tiêu định kỳ. */
    public void setRecurringExpenseId(String recurringExpenseId) {
        this.recurringExpenseId = recurringExpenseId;
    }

    /** Trả về mã người dùng. */
    public int getUserId() {
        return userId;
    }

    /** Cập nhật mã người dùng. */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** Trả về ngày đến hạn của kỳ. */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** Cập nhật ngày đến hạn của kỳ. */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /** Trả về số tiền của kỳ. */
    public double getAmount() {
        return amount;
    }

    /** Cập nhật số tiền của kỳ. */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /** Trả về trạng thái thực thi. */
    public RecurringExecutionStatus getStatus() {
        return status;
    }

    /** Cập nhật trạng thái thực thi. */
    public void setStatus(RecurringExecutionStatus status) {
        this.status = status;
    }

    /** Trả về mã giao dịch thực tế. */
    public String getTransactionId() {
        return transactionId;
    }

    /** Cập nhật mã giao dịch thực tế. */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "RecurringExecution{" +
                "id='" + id + '\'' +
                ", recurringExpenseId='" + recurringExpenseId + '\'' +
                ", userId=" + userId +
                ", dueDate=" + dueDate +
                ", amount=" + amount +
                ", status=" + status +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
