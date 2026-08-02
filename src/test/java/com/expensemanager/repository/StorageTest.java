package com.expensemanager.repository;

import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.category.Category;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.StorageType;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.factory.CategoryStorageFactory;
import com.expensemanager.factory.WalletStorageFactory;
import com.expensemanager.factory.BudgetStorageFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cho tầng Repository (Storage/CsvStorage/JsonStorage).
 *
 * Dùng @TempDir để JUnit tự tạo 1 thư mục tạm riêng cho mỗi lần test,
 * KHÔNG đụng vào file thật trong resources/data/ -> test xong tự xóa,
 * chạy nhiều lần không bị dính dữ liệu cũ.
 */
class StorageTest {

    // ================== CATEGORY ==================

    @Test
    void category_csv_luuVaDocLaiPhaiKhopDuLieu(@TempDir Path tempDir) throws IOException {
        Storage<Category> storage = CategoryStorageFactory.createStorage(StorageType.CSV);
        String filePath = tempDir.resolve("categories.csv").toString();

        List<Category> original = List.of(
                new Category("CAT01", "Ăn uống", "Chi tiêu ăn uống hàng ngày"),
                new Category("CAT02", "Di chuyển", "Xăng xe, Grab")
        );

        storage.save(filePath, original);
        List<Category> loaded = storage.load(filePath);

        assertEquals(2, loaded.size());
        assertEquals("Ăn uống", loaded.get(0).getName());
        assertEquals("CAT02", loaded.get(1).getId());
    }

    @Test
    void category_json_luuVaDocLaiPhaiKhopDuLieu(@TempDir Path tempDir) throws IOException {
        Storage<Category> storage = CategoryStorageFactory.createStorage(StorageType.JSON);
        String filePath = tempDir.resolve("categories.json").toString();

        List<Category> original = List.of(
                new Category("CAT01", "Ăn uống", "Chi tiêu ăn uống hàng ngày")
        );

        storage.save(filePath, original);
        List<Category> loaded = storage.load(filePath);

        assertEquals(1, loaded.size());
        assertEquals("Ăn uống", loaded.get(0).getName());
    }

    // ================== WALLET (trọng tâm: kiểm tra ĐA HÌNH) ==================

    @Test
    void wallet_csv_phaiGiuDungLoaiLopConSauKhiDocLai(@TempDir Path tempDir) throws IOException {
        Storage<Wallet> storage = WalletStorageFactory.createStorage(StorageType.CSV);
        String filePath = tempDir.resolve("wallets.csv").toString();

        Wallet bank = new BankAccount("W01", "Vietcombank", 1_000_000, 5_000);
        storage.save(filePath, List.of(bank));

        List<Wallet> loaded = storage.load(filePath);

        assertEquals(1, loaded.size());
        // Điểm quan trọng nhất: phải load ra ĐÚNG BankAccount, không phải Wallet chung
        assertInstanceOf(BankAccount.class, loaded.get(0));
        assertEquals(1_000_000, loaded.get(0).getBalance());
        assertEquals(WalletType.BANK, loaded.get(0).getType());
    }

    @Test
    void wallet_json_phaiGiuDungLoaiLopConSauKhiDocLai(@TempDir Path tempDir) throws IOException {
        Storage<Wallet> storage = WalletStorageFactory.createStorage(StorageType.JSON);
        String filePath = tempDir.resolve("wallets.json").toString();

        Wallet bank = new BankAccount("W01", "Vietcombank", 1_000_000, 5_000);
        storage.save(filePath, List.of(bank));

        List<Wallet> loaded = storage.load(filePath);

        assertEquals(1, loaded.size());
        assertInstanceOf(BankAccount.class, loaded.get(0));
        assertEquals(5_000, ((BankAccount) loaded.get(0)).getTransactionFee());
    }

    // ================== BUDGET (chỉ JSON, vì CSV deserializer chưa code) ==================

    @Test
    void budget_json_luuVaDocLaiPhaiKhopDuLieu(@TempDir Path tempDir) throws IOException {
        Storage<Budget> storage = BudgetStorageFactory.createStorage(StorageType.JSON);
        String filePath = tempDir.resolve("budgets.json").toString();

        Category category = new Category("CAT01", "Ăn uống", "");
        Budget budget = new Budget("B01", category, 2_000_000, Period.MONTH);

        storage.save(filePath, List.of(budget));
        List<Budget> loaded = storage.load(filePath);

        assertEquals(1, loaded.size());
        assertEquals(2_000_000, loaded.get(0).getLimitAmount());
        assertEquals(Period.MONTH, loaded.get(0).getPeriod());
    }

    // ================== TRƯỜNG HỢP BIÊN (edge case) ==================

    @Test
    void load_fileChuaTonTai_traVeDanhSachRong_khongNemLoi(@TempDir Path tempDir) throws IOException {
        Storage<Category> storage = CategoryStorageFactory.createStorage(StorageType.CSV);
        String filePath = tempDir.resolve("khong_ton_tai.csv").toString();

        List<Category> loaded = storage.load(filePath);

        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_fileJsonBiHong_phaiNemIOException(@TempDir Path tempDir) throws IOException {
        Storage<Category> storage = CategoryStorageFactory.createStorage(StorageType.JSON);
        Path badFile = tempDir.resolve("hong.json");
        java.nio.file.Files.writeString(badFile, "{ day khong phai json hop le");

        assertThrows(IOException.class, () -> storage.load(badFile.toString()));
    }
}