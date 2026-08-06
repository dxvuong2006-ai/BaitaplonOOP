package com.expensemanager.factory.storage;

import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.Income;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.transaction.Transaction;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.Function;

public class TransactionStorageFactory extends AbstractStorageFactory<Transaction> {

    @Override
    protected TypeToken<List<Transaction>> getTypeToken() {
        return new TypeToken<List<Transaction>>() {};
    }

    @Override
    protected String[] getCsvHeader() {
        return new String[]{
                "id",
                "amount",
                "date",
                "note",
                "categoryId",
                "walletId",
                "type",
                "extraField",
                "period"
        };
    }

    @Override
    protected Function<Transaction, String[]> getSerializer() {
        return tx -> new String[]{
                tx.getId(),
                String.valueOf(tx.getAmount()),
                tx.getDate().toString(),
                tx.getNote(),
                tx.getCategory() == null ? "" : tx.getCategory().getId(),
                tx.getWallet() == null ? "" : tx.getWallet().getId(),
                tx.getType().name(),
                extractExtraField(tx),
                extractPeriod(tx)
        };
    }

    @Override
    protected Function<String[], Transaction> getDeserializer() {
        return row -> {
            // Tầng Repository chỉ đọc String thuần túy.
            // Việc map Category ID và Wallet ID thành Object thật phải do ExpenseManager đảm nhiệm.
            throw new UnsupportedOperationException(
                    "Transaction deserializer must be handled by ExpenseManager/TransactionService."
            );
        };
    }

    /**
     * Trích xuất trường phụ: source (của Income) hoặc paymentMethod (của Expense)
     */
    private String extractExtraField(Transaction tx) {
        if (tx instanceof Income) {
            return ((Income) tx).getSource();
        }
        if (tx instanceof Expense) {
            return ((Expense) tx).getPaymentMethod();
        }
        return "";
    }

    /**
     * Trích xuất chu kỳ: Chỉ RecurringExpense mới có Period
     */
    private String extractPeriod(Transaction tx) {
        if (tx instanceof RecurringExpense) {
            return ((RecurringExpense) tx).getPeriod().name();
        }
        return "";
    }
}