package com.expensemanager.service;

import com.expensemanager.exception.*;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    @Test
    void testValidateAmount_Valid() {
        assertDoesNotThrow(() -> ValidationService.validateAmount(100000));
    }

    @Test
    void testValidateAmount_Negative() {
        assertThrows(NegativeValueException.class,
                () -> ValidationService.validateAmount(-100));
    }

    @Test
    void testValidateCategory_Valid() {
        Category category = new Category("C01", "Ăn uống", "Chi tiêu ăn uống");
        assertDoesNotThrow(() -> ValidationService.validateCategory(category));
    }

    @Test
    void testValidateCategory_Null() {
        assertThrows(EmptyFieldException.class,
                () -> ValidationService.validateCategory(null));
    }

    @Test
    void testValidateBudget_Valid() {
        Category category = new Category("C01", "Ăn uống", "Chi tiêu");
        Budget budget = new Budget("B01", category, 500000, Period.MONTH);

        assertDoesNotThrow(() -> ValidationService.validateBudget(budget));
    }

    @Test
    void testValidateBudget_Null() {
        assertThrows(EmptyFieldException.class,
                () -> ValidationService.validateBudget(null));
    }

    @Test
    void testValidateWalletName_Valid() {
        List<Wallet> wallets = new ArrayList<>();
        wallets.add(new CashWallet("W01", "Ví tiền mặt", 1000000));

        assertDoesNotThrow(() ->
                ValidationService.validateWalletName(wallets, "Ví phụ"));
    }

    @Test
    void testValidateWalletName_Duplicate() {
        List<Wallet> wallets = new ArrayList<>();
        wallets.add(new CashWallet("W01", "Ví tiền mặt", 1000000));

        assertThrows(DuplicateEntityException.class,
                () -> ValidationService.validateWalletName(wallets, "Ví tiền mặt"));
    }

    @Test
    void testValidateWalletName_Empty() {
        List<Wallet> wallets = new ArrayList<>();

        assertThrows(EmptyFieldException.class,
                () -> ValidationService.validateWalletName(wallets, ""));
    }

    @Test
    void testValidateWithdraw_EnoughMoney() {
        Wallet wallet = new CashWallet("W01", "Ví", 1000000);

        assertDoesNotThrow(() ->
                ValidationService.validateWithdraw(wallet, 200000));
    }

    @Test
    void testValidateWithdraw_InsufficientFunds() {
        Wallet wallet = new CashWallet("W01", "Ví", 100000);

        assertThrows(InsufficientFundsException.class,
                () -> ValidationService.validateWithdraw(wallet, 200000));
    }

    @Test
    void testValidateWithdraw_NullWallet() {
        assertThrows(EmptyFieldException.class,
                () -> ValidationService.validateWithdraw(null, 100000));
    }

    @Test
    void testValidateWithdraw_InvalidAmount() {
        Wallet wallet = new CashWallet("W01", "Ví", 1000000);

        assertThrows(NegativeValueException.class,
                () -> ValidationService.validateWithdraw(wallet, -100));
    }

    @Test
    void testValidateDateRange_Valid() {
        assertDoesNotThrow(() ->
                ValidationService.validateDateRange(
                        LocalDate.now().minusDays(5),
                        LocalDate.now()));
    }

    @Test
    void testValidateDateRange_StartAfterEnd() {
        assertThrows(InvalidFormatException.class,
                () -> ValidationService.validateDateRange(
                        LocalDate.now(),
                        LocalDate.now().minusDays(1)));
    }

    @Test
    void testValidateDateRange_Null() {
        assertThrows(EmptyFieldException.class,
                () -> ValidationService.validateDateRange(
                        null,
                        LocalDate.now()));
    }
}
