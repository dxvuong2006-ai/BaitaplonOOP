package com.expensemanager.model.transaction;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.wallet.Wallet;
import java.time.LocalDate;

/** Class thu nhập. */
public class Income extends Transaction {
    private String source;

    /** Constructor rỗng phục vụ cho các thư viện Serialize/Deserialize (JSON, XML). */
    public Income() {
        super();
    }

    /** Khởi tạo một khoản thu nhập. */
    public Income(String id, double amount, LocalDate date, String note, Category category,
                  Wallet wallet, String source, int userId) {
        super(id, amount, date, note, category, wallet, userId);
        setSource(source);
    }

    /** Trả về loại giao dịch là thu nhập (INCOME). */
    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }

    /** Trả về số tiền có dấu, luôn dương vì đây là khoản thu làm tăng tài sản. */
    @Override
    public double getSignedAmount() {
        return getAmount();
    }

    /** Lấy nguồn thu nhập. */
    public String getSource() {
        return source;
    }

    /** Gán lại nguồn thu nhập. */
    public void setSource(String source) {
        if (source == null) {
            throw new EmptyFieldException(FieldType.SOURCE);
        }
        this.source = source;
    }
}