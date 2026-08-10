package com.expensemanager.model.transaction;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.wallet.Wallet;
import java.time.LocalDate;

/** Class chi tiêu. */
public class Expense extends Transaction {
    private String paymentMethod;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public Expense() {
        super();
    }

    /** Khởi tạo một khoản chi tiêu. */
    public Expense(String id, double amount, LocalDate date, String note, Category category,
                   Wallet wallet, String paymentMethod, int userId) {
        super(id, amount, date, note, category, wallet, userId);
        setPaymentMethod(paymentMethod);
    }

    /** Trả về loại giao dịch là chi tiêu (EXPENSE). */
    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }

    /** Trả về số tiền có dấu, luôn âm vì đây là khoản chi làm giảm tài sản. */
    @Override
    public double getSignedAmount() {
        return -getAmount(); // Trả về số âm (-amount) thể hiện sự suy giảm tài sản
    }

    /** Lấy phương thức thanh toán. */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /** Gán lại phương thức thanh toán. */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}