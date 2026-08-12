package com.expensemanager.factory.model;

import com.expensemanager.exception.EmptyFieldException; // Thêm import để bắt lỗi null
import com.expensemanager.exception.InvalidFormatException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType; // Thêm import FieldType
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;

import java.time.LocalDate;

public final class TransactionFactory {

    private TransactionFactory() {
        // Không cho phép khởi tạo Factory
    }

    public static Transaction createTransaction(
            TransactionType type,
            String id,
            double amount,
            LocalDate date,
            String note,
            Category category,
            Wallet wallet,
            String source,
            String paymentMethod,
            Period period,
            int userId,
            LocalDate nextDueDate,
            boolean active
    ) throws InvalidFormatException {
        // 1. Chặn lỗi NullPointerException trước khi vào switch-case
        if (type == null) {
            throw new EmptyFieldException(FieldType.TRANSACTIONTYPE);
        }
        switch (type) {
            case INCOME:
                return new Income(
                        id,
                        amount,
                        date,
                        note,
                        category,
                        wallet,
                        source,
                        userId
                );
            case EXPENSE:
                return new Expense(
                        id,
                        amount,
                        date,
                        note,
                        category,
                        wallet,
                        paymentMethod,
                        userId
                );
            case RECURRING_EXPENSE:
                if (nextDueDate == null) {
                    return new RecurringExpense(
                            id,
                            amount,
                            date,
                            note,
                            category,
                            wallet,
                            paymentMethod,
                            period,
                            userId
                    );
                }
                return new RecurringExpense(
                        id,
                        amount,
                        date,
                        note,
                        category,
                        wallet,
                        paymentMethod,
                        period,
                        userId,
                        nextDueDate,
                        active
                );
            default:
                // 2. Ném ra ngoại lệ chuẩn với FieldType và giá trị gây lỗi
                throw new InvalidFormatException(FieldType.TRANSACTIONTYPE, String.valueOf(type));
        }
    }
}
