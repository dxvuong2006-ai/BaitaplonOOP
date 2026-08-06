package com.expensemanager.service;

import com.expensemanager.exception.*;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;

import java.util.List;
import java.time.LocalDate;

/** Lớp kiểm tra dữ liệu và các quy tắc nghiệp vụ trước khi thao tác. */
public class ValidationService {

    /** Kiểm tra giao dịch rút tiền(Tính thêm phí giao dịch nếu dùng ngân hàng). */
    public static void validateWithdraw(Wallet wallet, double amountToWithdraw) {
        validateWallet(wallet);
        validateAmount(amountToWithdraw);
        double totalRequired = amountToWithdraw;

        //Kiểm tra nếu dùng ngân hàng thì cộng thêm phí.
        if (wallet instanceof BankAccount) {
            totalRequired += ((BankAccount) wallet).getTransactionFee();
        }

        //Kiểm tra số dư có đủ để rút không.
        if (totalRequired > (wallet.getBalance() + CurrencyUtils.EPSILON)) {
            throw new InsufficientFundsException(wallet.getBalance(), totalRequired);
        }
    }

    /** Kiểm tra tên ví trước khi tạo ví mới. */
    public static void validateWalletName(List<Wallet> existingWallet, String name) {
        validateWallets(existingWallet);
        if (name == null || name.trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
        String walletName = name.trim();
        for (Wallet wallet : existingWallet) {
            if (wallet == null) {
                continue;
            }
            if (wallet.getName().equalsIgnoreCase(walletName)) {
                throw new DuplicateEntityException("Ví", wallet.getName());
            }
        }
    }

    /** Kiểm tra giao dịch có rỗng không */
    public static void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new EmptyFieldException(FieldType.TRANSACTION);
        }
    }

    /** Kiểm tra danh sách giao dịch có rỗng không */
    public static void validateTransactions(List<Transaction> transactions) {
        if (transactions == null) {
            throw new EmptyFieldException(FieldType.TRANSACTION);
        }
    }

    /** Kiểm tra ví có rỗng không. */
    public static void validateWallet(Wallet wallet) {
        if (wallet == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
        if (wallet.getName() == null || wallet.getName().trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
    }

    /** Kiểm tra ví có rỗng không. */
    public static void validateWallets(List<Wallet> wallets) {
        if (wallets == null) {
            throw new EmptyFieldException(FieldType.WALLET);
        }
    }

    /** Kiểm tra xem loại có rỗng không. */
    public static void validateCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.NAME);
        }
    }

    /** Kiểm tra ngân sách có rỗng không. */
    public static void validateBudget(Budget budget) {
        if (budget == null) {
            throw new EmptyFieldException(FieldType.CATEGORY);
        }
        validateCategory(budget.getCategory());
        validateAmount(budget.getLimitAmount());
    }

    /** Kiểm tra xem lượng tiền có hợp lệ không. */
    public static void validateAmount(double amount) {
        if (!CurrencyUtils.isPositiveAmount(amount)) {
            throw new NegativeValueException(FieldType.AMOUNT);
        }
    }

    /** Kiểm tra xem ngày có hợp lệ không. */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new EmptyFieldException(FieldType.DATE);
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidFormatException(FieldType.DATE, "Khoảng thời gian không hợp lệ");
        }
    }
}
