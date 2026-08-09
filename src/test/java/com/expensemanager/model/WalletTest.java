package com.expensemanager.model;

import com.expensemanager.exception.InsufficientFundsException;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

    private Wallet bankWallet;
    private Wallet cashWallet;

    @BeforeEach
    public void setUp() {
        // Khởi tạo dữ liệu chuẩn trước mỗi test case
        bankWallet = new BankAccount("W01", "Vietcombank", 5000000, 1100); // Phí giao dịch 1100đ
        cashWallet = new CashWallet("W02", "Ví tiền mặt", 2000000); // Tiền mặt không có phí
    }

    @Test
    public void testCreateWallet_WithNegativeBalance_ShouldThrowException() {
        // Kiểm tra validation không cho phép tạo ví có số dư âm
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CashWallet("W03", "Ví Lỗi", -50000);
        });
        assertTrue(exception.getMessage().contains("không được âm"));
    }

    @Test
    public void testDeposit_Success() {
        // Nạp tiền hợp lệ
        bankWallet.deposit(1000000);
        assertEquals(6000000, bankWallet.getBalance(), "Số dư phải được cộng thêm 1 triệu");
    }

    @Test
    public void testDeposit_NegativeAmount_ShouldThrowException() {
        // Nạp tiền âm (Lỗi logic)
        assertThrows(IllegalArgumentException.class, () -> {
            bankWallet.deposit(-10000);
        });
    }

    @Test
    public void testWithdraw_Polymorphism_BankWallet_Success() {
        // Rút 1 triệu từ tài khoản ngân hàng (Phải trừ thêm phí 1100đ)
        bankWallet.withdraw(1000000);
        assertEquals(3998900, bankWallet.getBalance(), "Số dư sau khi trừ tiền và phí không khớp");
    }

    @Test
    public void testWithdraw_InsufficientFunds_ShouldThrowException() {
        // Rút quá số dư -> Kỳ vọng ném ra InsufficientFundsException
        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            cashWallet.withdraw(3000000); // Trong ví chỉ có 2 triệu
        });
        assertNotNull(exception);
    }
}