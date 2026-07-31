package com.expensemanager.service;

import com.expensemanager.exception.*;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;


import java.util.List;
import java.time.LocalDate;

/** Lớp kiểm tra dữ liệu và các quy tắc nghiệp vụ trước khi thao tác. */
public class ValidationService {

    /** Khỏi tạo. */
    private ValidationService() {
    }

    /** Kiểm tra giao dịch rút tiền(Tính thêm phí giao dịch nếu dùng ngân hàng). */
    public static void validateWithdraw(Wallet wallet, double amountToWithdraw) {
        if (wallet == null) {
            throw new EmptyFieldException();
        }
        if (!CurrencyUtils.isPositiveAmount(amountToWithdraw)) {
            throw new NegativeValueException();
        }
        double totalRequired = amountToWithdraw;

        //Kiểm tra nếu dùng ngân hàng thì cộng thêm phí.
        if (wallet instanceof BankAccount) {
            totalRequired += ((BankAccount) wallet).getTransactionFee();
        }

        //Kiểm tra số dư có đủ để rút không.
        if (totalRequired > (wallet.getBalance() + CurrencyUtils.EPSILON)) {
            throw new InsufficientFundsException();
        }
    }

    /** Kiểm tra tên ví trước khi tạo giao dịch mới. */
    public static void validateWalletName(List<Wallet> existingWallet, String name) {
        if (existingWallet == null) {
            throw new EmptyFieldException();
        }
        if (name == null || name.trim().isEmpty()) {
            throw new EmptyFieldException();
        }
        String walletName = name.trim();
        for (Wallet wallet : existingWallet) {
            if (wallet == null) {
                continue;
            }
            if (wallet.getName().equalsIgnoreCase(walletName)) {
                throw new DuplicateEntityException();
            }
        }
    }

    /** Kiểm tra xem loại có rỗng không. */
    public static void validateCategory(Category category) {
        if (category == null) {
            throw new EmptyFieldException();
        }
        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {
            throw new EmptyFieldException();
        }
    }

    /** Kiểm tra ngân sách có rỗng không. */
    public static void validateBudget(Budget budget) {
        if (budget == null) {
            throw new EmptyFieldException();
        }
        validateCategory(budget.getCategory());
        validateAmount(budget.getLimitAmount());
    }

    /** Kiểm tra xem lượng tiền có hợp lệ không. */
    public static void validateAmount(double amount) {
        if (!CurrencyUtils.isPositiveAmount(amount)) {
            throw new InvalidFormatException();
        }
    }

    /** Kiểm tra xem ngày có hợp lệ không. */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new EmptyFieldException();
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDataException();
        }
    }
}
