package com.expensemanager.service;

import com.expensemanager.exception.InsufficientFundsException;
import com.expensemanager.exception.NegativeValueException;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra logic Nạp/Rút tiền ví và Validation rút tiền")
class WalletServiceAndWithdrawTest {

    private Wallet normalWallet;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        normalWallet = new CashWallet("W1", "Ví tiền mặt", 500000.0, 1);
        // BankAccount giả định có số dư ban đầu là 1,000,000 và phí giao dịch là 5,000
        bankAccount = new BankAccount("W2", "Tài khoản VCB", 1000000.0, 5000.0, 1);
    }

    @Test
    @DisplayName("Nạp tiền vào ví thường tăng số dư đúng")
    void testDepositNormalWallet() {
        normalWallet.deposit(200000);
        assertEquals(700000, normalWallet.getBalance(), 0.001, "Số dư sau nạp phải là 700,000");
    }

    @Test
    @DisplayName("Rút tiền hợp lệ từ ví thường")
    void testValidateWithdrawNormalWalletSuccess() {
        assertDoesNotThrow(() -> ValidationService.validateWithdraw(normalWallet, 300000));
        normalWallet.withdraw(300000);
        assertEquals(200000, normalWallet.getBalance(), 0.001, "Số dư còn lại phải là 200,000");
    }

    @Test
    @DisplayName("Rút tiền từ tài khoản ngân hàng tính cả phí giao dịch")
    void testValidateWithdrawBankAccountIncludesFee() {
        // Số dư bankAccount = 1,000,000. Phí = 5,000. Rút 996,000 -> Tổng cần = 1,001,000 (Vượt quá)
        assertThrows(InsufficientFundsException.class, () -> {
            ValidationService.validateWithdraw(bankAccount, 996000);
        }, "Phải báo lỗi thiếu tiền vì cần 996,000 + 5,000 phí > 1,000,000");

        // Rút 990,000 -> Tổng cần = 995,000 <= 1,000,000 (Thành công)
        assertDoesNotThrow(() -> ValidationService.validateWithdraw(bankAccount, 990000));
    }

    @Test
    @DisplayName("Rút tiền vượt quá số dư ví thường ném InsufficientFundsException")
    void testWithdrawExceedsBalanceThrowsException() {
        assertThrows(InsufficientFundsException.class, () -> {
            ValidationService.validateWithdraw(normalWallet, 600000);
        }, "Rút 600,000 từ ví 500,000 phải ném InsufficientFundsException");
    }

    @Test
    @DisplayName("Rút/Nạp số tiền âm hoặc bằng 0 ném NegativeValueException")
    void testInvalidAmountThrowsException() {
        assertThrows(NegativeValueException.class, () -> {
            ValidationService.validateWithdraw(normalWallet, -50000);
        }, "Rút số tiền âm phải ném NegativeValueException");

        assertThrows(NegativeValueException.class, () -> {
            ValidationService.validateAmount(0);
        }, "Số tiền bằng 0 phải ném NegativeValueException");
    }
}
