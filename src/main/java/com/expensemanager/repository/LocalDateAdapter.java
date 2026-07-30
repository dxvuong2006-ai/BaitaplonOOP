package com.expensemanager.repository;

import com.expensemanager.utils.DateUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * SỬA so với bản trước của tao: dùng lại {@link DateUtils#formatDate} /
 * {@link DateUtils#parseDate} (định dạng dd/MM/yyyy) thay vì tự hard-code
 * ISO_LOCAL_DATE. Lý do: project đã có DateUtils làm "nguồn chân lý" duy
 * nhất cho định dạng ngày (dùng khi hiển thị CLI/GUI); nếu JsonStorage tự
 * dùng định dạng khác thì ngày lưu trong file JSON sẽ không khớp với ngày
 * hiển thị/nhập ở nơi khác trong ứng dụng.
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateUtils.formatDate(src));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return DateUtils.parseDate(json.getAsString());
        } catch (Exception e) {
            throw new JsonParseException("Ngày trong file dữ liệu không hợp lệ: " + json.getAsString(), e);
        }
    }
}