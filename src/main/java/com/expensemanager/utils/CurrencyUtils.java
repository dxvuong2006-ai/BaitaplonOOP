package com.expensemanager.utils;

import java.text.NumberFormat;
import java.util.Locale;

/** Lop CurrencyUtils xử lý tiền tệ. */
public class CurrencyUtils {
    /** Ngăn không cho tạo đối tượng từ bên ngoài. */
    private CurrencyUtils() {
        throw new UnsupportedOperationException("Utility class không thể khởi tạo!");
    }

    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");

    /** Định dạng số tiền dạng double thành chuỗi VND (VD: 50000 -> 50.000 ₫). */
    public static String formatVND(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
        return formatter.format(amount);
    }

    /** Chuyển chuỗi tiền tệ về giá trị số double. */
    public static double parseAmount(String amountStr) throws NumberFormatException {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new NumberFormatException("Số tiền không được để trống!");
        }
        String cleanStr = amountStr.replaceAll("[^0-9.]", "");
        return Double.parseDouble(cleanStr);
    }
}
