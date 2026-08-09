package com.expensemanager.model.transaction;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.wallet.Wallet;
import java.time.LocalDate;

public class Expense extends Transaction {
    private String paymentMethod;
    public Expense() {
        super();
    }

    public Expense(String id, double amount, LocalDate date, String note, Category category,
                   Wallet wallet, String paymentMethod, int userId) {
        super(id, amount, date, note, category, wallet, userId);
        setPaymentMethod(paymentMethod);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }

    @Override
    public double getSignedAmount() {
        return -getAmount(); // Trả về số âm (-amount) thể hiện sự suy giảm tài sản
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}