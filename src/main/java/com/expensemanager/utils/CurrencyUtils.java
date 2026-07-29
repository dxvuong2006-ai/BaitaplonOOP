package com.expensemanager.utils;

import java.text.NumberFormat;
import java.util.Locale;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;

public class CurrencyUtils {
    /** Ngăn không cho khởi tạo đối tượng bên ngoài do các phương thức đều là static. */
    private CurrencyUtils() {}

    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    public static final double EPSILON = 0.0001;

    /** Định dạng số tiền nhập vào thành dạng tiền Việt Nam(Để hiển thị). */
    public static String formatVND(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
        return formatter.format(amount);
    }

    /** Chuyển chuỗi tiền Việt Nam thành dạng số(Để tính toán). */
    public static double parseAmount(String amountStr) throws NumberFormatException,EmptyFieldException {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new EmptyFieldException(FieldType.AMOUNTSTR);
        }
        String cleanStr = amountStr.replace("₫", "")
                .replace("VND", "")
                .replace("vnd", "")
                .replace(".", "")
                .trim();
        return Double.parseDouble(cleanStr);
    }

    /** Kiểm tra xem có thể chuyển chuỗi thành dạng số được không. */
    public static boolean isValidAmount(String amountStr) {
        try {
            parseAmount(amountStr);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /** Kiểm tra số tiền nhập vào có lớn hơn 0 không. */
    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }

    /** So sánh 2 giá trị số thực. */
    public static boolean isEqual(double amount1, double amount2) {
        return Math.abs(amount1 - amount2) < EPSILON;
    }

    /** Làm tròn tiền thành lên hàng đơn vị. */
    public static double roundToDong(double amount) {
        return Math.round(amount);
    }

    /** Chuyển đổi tỷ giá ngoại tệ. */
}
