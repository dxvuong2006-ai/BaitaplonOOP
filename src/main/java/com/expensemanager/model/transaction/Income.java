package com.expensemanager.model.transaction;

import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.exception.EmptyFieldException;
import java.time.LocalDate;

/** Class thu nhập. */
public class Income extends Transaction {
    private String source;
    public Income() {
        super();
    }

    public Income(String id, double amount, LocalDate date, String note, Category category,
                  Wallet wallet, String source, int userId) {
        super(id, amount, date, note, category, wallet, userId);
        setSource(source);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }

    @Override
    public double getSignedAmount() {
        return getAmount();
    }

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        if (source == null) {
            throw new EmptyFieldException(FieldType.SOURCE);
        }
        this.source = source;
    }
}