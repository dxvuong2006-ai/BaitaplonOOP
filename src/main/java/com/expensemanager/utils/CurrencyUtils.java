package com.expensemanager.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lop tien ich (utility class) xu ly dinh dang va phan tich tien te.
 *
 * <p>Don vi tien te mac dinh cua ung dung la VND
 * de dam bao moi noi trong chuong trinh hien thi/doc tien theo cung mot chuan.
 * <p>Day la utility class (chi chua static method), khong the khoi tao doi tuong.
 */
public final class CurrencyUtils {

    /** Locale Viet Nam, dung de dinh dang tien te theo chuan VND (vi du: 50.000 ₫). */
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");

    /** Dinh dang so nguyen co dau phan cach hang nghin, dung khi hien thi so du/tong tien. */
    private static final String GROUPED_NUMBER_PATTERN = "#,###";

    /** Ky hieu tien te VND dung khi format thu cong (khong qua NumberFormat). */
    private static final String VND_SUFFIX = " VND";

    /** Nguong sai so cho phep khi so sanh hai gia tri tien te dang double. */
    private static final double EPSILON = 0.0001;

    /** Ngan khong cho tao doi tuong tu ben ngoai. */
    private CurrencyUtils() {
        throw new UnsupportedOperationException("Utility class khong the khoi tao!");
    }

    /**
     * Dinh dang so tien dang double thanh chuoi VND theo chuan Locale Viet Nam.
     * Vi du: 50000 -> "50.000 ₫".
     */
    public static String formatVND(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
        formatter.setMaximumFractionDigits(0);
        String formatted = formatter.format(amount);
        return formatted.replace('\u00A0', ' ').trim();
    }

    /**
     * Dinh dang so tien co dau cong/tru truoc gia tri, dung de hien thi giao dich
     * thu (+) hoac chi (-) trong bang danh sach giao dich.
     * Vi du: +50.000 ₫ (thu), -20.000 ₫ (chi).
     */
    public static String formatSignedVND(double signedAmount) {
        String formatted = formatVND(Math.abs(signedAmount));
        return signedAmount < 0 ? "-" + formatted : "+" + formatted;
    }

    /**
     * Dinh dang so tien don gian, khong kem ky hieu tien te, chi co dau phan
     * cach hang nghin. Vi du: 1234567 -> "1.234.567".
     * Huu ich khi ghi ra file CSV hoac hien thi gon trong bang thong ke.
     */
    public static String formatNumber(double amount) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(VIETNAM_LOCALE);
        DecimalFormat decimalFormat = new DecimalFormat(GROUPED_NUMBER_PATTERN, symbols);
        return decimalFormat.format(amount);
    }

    /**
     * Chuyen chuoi tien te (co the chua ky hieu ₫, VND, dau phan cach hang nghin)
     * ve gia tri so double. Ho tro ca dinh dang Viet Nam (dau "." phan cach hang
     * nghin, dau "," phan cach thap phan) va dinh dang thong thuong (dau "," phan
     * cach hang nghin, dau "." phan cach thap phan).
     * <p>Vi du hop le: "50.000", "50,000.50", "50000", "50.000 ₫", "1.000.000 VND".
     */
    public static double parseAmount(String amountStr) throws NumberFormatException {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new NumberFormatException("So tien khong duoc de trong!");
        }

        // Loai bo ky hieu tien te va khoang trang thua (₫, VND, USD...)
        String cleanStr = amountStr.trim()
                .replace("₫", "")
                .replaceAll("(?i)vnd", "")
                .replaceAll("[^0-9.,\\-]", "")
                .trim();

        // Kiem tra neu rong thi nem ra ngoai le
        if (cleanStr.isEmpty()) {
            throw new NumberFormatException("So tien khong hop le: " + amountStr);
        }

        boolean isNegative = cleanStr.startsWith("-");
        if (isNegative) {
            cleanStr = cleanStr.substring(1);
        }

        int lastDot = cleanStr.lastIndexOf('.');
        int lastComma = cleanStr.lastIndexOf(',');

        // Xac dinh dau phan cach thap phan la dau ("." hay ",") xuat hien sau cung
        if (lastDot != -1 && lastComma != -1) {
            if (lastDot > lastComma) {
                // Dang "1,000,000.50" -> dau "," la phan cach hang nghin
                cleanStr = cleanStr.replace(",", "");
            } else {
                // Dang "1.000.000,50" -> dau "." la phan cach hang nghin
                cleanStr = cleanStr.replace(".", "").replace(",", ".");
            }
        } else if (lastComma != -1) {
            // Chi co dau ",": xem la phan cach thap phan (VD: "50000,5")
            cleanStr = cleanStr.replace(",", ".");
        } else if (lastDot != -1) {
            // Chi co dau ".": neu co dung 3 chu so sau dau cuoi va co nhieu hon 1
            // nhom thi coi la phan cach hang nghin kieu VN (VD: "50.000")
            String afterLastDot = cleanStr.substring(lastDot + 1);
            long dotCount = cleanStr.chars().filter(c -> c == '.').count();
            if (afterLastDot.length() == 3 && dotCount >= 1 && cleanStr.indexOf('.') != lastDot) {
                cleanStr = cleanStr.replace(".", "");
            } else if (afterLastDot.length() == 3 && dotCount == 1
                    && cleanStr.length() - lastDot - 1 == 3
                    && cleanStr.substring(0, lastDot).length() <= 3) {
                // Truong hop mot nhom duy nhat vd "50.000" -> hang nghin VN
                cleanStr = cleanStr.replace(".", "");
            }
            // Nguoc lai giu nguyen, coi dau "." la phan cach thap phan (chuan Java)
        }

        try {
            double result = Double.parseDouble(cleanStr);
            return isNegative ? -result : result;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("So tien khong hop le: " + amountStr);
        }
    }

    /**
     * Kiem tra mot chuoi co phai la so tien hop le hay khong, khong nem ngoai le.
     * Dung de validate du lieu nhap tu nguoi dung truoc khi goi {@link #parseAmount}.
     */
    public static boolean isValidAmount(String amountStr) {
        try {
            parseAmount(amountStr);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Kiem tra so tien co hop le cho giao dich hay khong (phai la so duong,
     * khac 0). Dung trong Transaction/Wallet de tu choi so tien am hoac bang 0.
     */
    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }

    /**
     * So sanh hai gia tri tien te co xap xi bang nhau khong, tranh sai so
     * khi so sanh truc tiep hai so double (VD: kiem tra so du vi con lai == 0).
     */
    public static boolean isEqual(double amount1, double amount2) {
        return Math.abs(amount1 - amount2) < EPSILON;
    }

    /**
     * Lam tron so tien ve don vi dong (khong lay phan thap phan).
     * Vi VND thuc te khong su dung phan le.
     */
    public static double roundToDong(double amount) {
        return Math.round(amount);
    }
}
