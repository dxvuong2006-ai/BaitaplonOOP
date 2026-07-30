package com.expensemanager.repository;

import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.BankAccount;
import com.expensemanager.model.wallet.CashWallet;
import com.expensemanager.model.wallet.EWallet;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Giữ cách khởi tạo bằng TypeToken<List<T>> của AI kia (linh hoạt hơn cách
 * dùng Class<T> của tao trước đây), kết hợp PolymorphicAdapter đã vá lỗi và
 * LocalDateAdapter dùng lại DateUtils.
 *
 * @param <T> kiểu đối tượng cần lưu trữ
 */
public class JsonStorage<T> implements Storage<T> {

    private final Gson gson;
    private final Type listType;

    public JsonStorage(TypeToken<List<T>> typeToken) {
        this.listType = typeToken.getType();
        this.gson = buildGson();
    }

    private static Gson buildGson() {
        Map<String, Class<? extends Wallet>> walletRegistry = Map.of(
                "CashWallet", CashWallet.class,
                "BankAccount", BankAccount.class,
                "EWallet", EWallet.class
        );

        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Wallet.class, new PolymorphicAdapter<>(walletRegistry))
                // TODO: khi Income/Expense/RecurringExpense đã sẵn sàng, thêm registry
                // tương tự cho Transaction.class rồi bật dòng dưới:
                // .registerTypeAdapter(Transaction.class, new PolymorphicAdapter<>(transactionRegistry))
                .create();
    }

    @Override
    public List<T> load(String filePath) throws IOException {
        List<String> lines = FileUtils.readAllLines(filePath);
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }
        String content = String.join("\n", lines);
        if (content.isBlank()) {
            return new ArrayList<>();
        }

        try {
            List<T> data = gson.fromJson(content, listType);
            return data != null ? data : new ArrayList<>();
        } catch (Exception e) {
            // Bọc lại thành IOException để tầng gọi (Service/Repository) xử lý
            // đồng nhất với các lỗi đọc file khác, thay vì để lộ chi tiết Gson.
            throw new IOException("File dữ liệu JSON bị hỏng hoặc sai định dạng: " + filePath, e);
        }
    }

    @Override
    public void save(String filePath, List<T> data) throws IOException {
        String json = gson.toJson(data != null ? data : new ArrayList<T>());
        FileUtils.writeAllLines(filePath, List.of(json.split("\n")));
    }
}