package com.expensemanager.repository;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Giữ nguyên ý tưởng hay của AI kia: 1 Adapter GENERIC dùng chung được cho
 * mọi abstract class (Wallet, Transaction...) thay vì phải viết riêng từng
 * cái như WalletTypeAdapter trước đây của tao -> đỡ lặp code.
 *
 * ĐIỂM ĐÃ SỬA so với bản gốc (rất quan trọng):
 * Bản gốc dùng `Class.forName(tenLopDayDu)` để dựng lại object -> để hệ
 * thống tự "new" ra BẤT KỲ class nào có mặt trong classpath dựa theo 1
 * chuỗi text đọc từ file dữ liệu. Đây là 1 lỗ hổng deserialization kinh
 * điển: nếu file dữ liệu (categories.json, wallets.json...) bị chỉnh sửa
 * tay hoặc bị thay bởi file độc hại, chuỗi classname đó có thể trỏ tới bất
 * kỳ class nào khác trong ứng dụng, không chỉ Wallet/Transaction.
 * Ngoài ra, nếu nhóm sau này đổi tên gói (refactor) thì các file dữ liệu cũ
 * đã lưu sẽ lập tức hỏng vì tên class đầy đủ không còn tồn tại.
 *
 * Cách sửa: chỉ cho phép deserialize về những lớp đã được khai báo tường
 * minh (whitelist) trong registry truyền vào từ bên ngoài, dựa trên
 * SIMPLE NAME (VD: "BankAccount") thay vì tên class đầy đủ.
 *
 * @param <T> kiểu abstract cần xử lý đa hình (Wallet, Transaction...)
 */
public class PolymorphicAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    // ĐÃ SỬA LỖI: trước đây key này là "type", bị TRÙNG với field "type"
    // (kiểu WalletType) đã có sẵn trong lớp Wallet -> khi serialize, 2 field
    // cùng tên "type" đè lên nhau, khiến lúc đọc lại nhận nhầm giá trị
    // "BANK"/"CASH"/"EWALLET" (của field domain) làm tên lớp con, gây lỗi
    // JsonParseException. Đổi sang "@class" để chắc chắn không trùng với
    // field của bất kỳ Model nào (Wallet, Transaction, Budget...).
    private static final String CLASS_META_KEY = "@class";

    private final Map<String, Class<? extends T>> registry;

    /**
     * @param registry ánh xạ "tên lớp con" -> Class tương ứng, VD:
     *                 Map.of("CashWallet", CashWallet.class,
     *                        "BankAccount", BankAccount.class,
     *                        "EWallet", EWallet.class)
     */
    public PolymorphicAdapter(Map<String, Class<? extends T>> registry) {
        if (registry == null || registry.isEmpty()) {
            throw new IllegalArgumentException("registry không được rỗng");
        }
        this.registry = registry;
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObj = context.serialize(src, src.getClass()).getAsJsonObject();
        jsonObj.addProperty(CLASS_META_KEY, src.getClass().getSimpleName());
        return jsonObj;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();
        JsonElement typeElement = jsonObj.get(CLASS_META_KEY);
        if (typeElement == null) {
            throw new JsonParseException("Thiếu trường \"" + CLASS_META_KEY + "\" khi đọc JSON.");
        }

        String typeName = typeElement.getAsString();
        Class<? extends T> target = registry.get(typeName);
        if (target == null) {
            throw new JsonParseException("Không hỗ trợ loại \"" + typeName
                    + "\". Các loại hợp lệ: " + registry.keySet());
        }
        return context.deserialize(jsonObj, target);
    }
}