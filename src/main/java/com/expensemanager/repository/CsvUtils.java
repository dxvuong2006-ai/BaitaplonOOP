package com.expensemanager.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Bộ công cụ escape/parse CSV theo chuẩn RFC 4180 tối giản.
 *
 * Vì sao cần lớp này: nếu chỉ nối chuỗi bằng dấu phẩy và tách bằng
 * line.split(","), dữ liệu như Category.description = "Ăn sáng, cà phê"
 * hoặc Transaction.note chứa dấu phẩy sẽ bị TÁCH SAI CỘT, gây lệch toàn bộ
 * dữ liệu phía sau. Quy tắc xử lý: nếu 1 field chứa dấu phẩy, dấu ngoặc kép,
 * hoặc xuống dòng thì phải bọc field đó trong dấu ngoặc kép " ", và mọi dấu
 * ngoặc kép bên trong phải nhân đôi thành "".
 */
public final class CsvUtils {

    private CsvUtils() {}

    /** Escape 1 giá trị field trước khi ghi vào dòng CSV. */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needQuote = value.contains(",") || value.contains("\"") || value.contains("\n");
        String escaped = value.replace("\"", "\"\"");
        return needQuote ? "\"" + escaped + "\"" : escaped;
    }

    /** Nối các field đã escape thành 1 dòng CSV hoàn chỉnh. */
    public static String joinRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    /** Tách 1 dòng CSV thành mảng field, hiểu đúng field có chứa dấu phẩy/ngoặc kép. */
    public static String[] parseRow(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    boolean isEscapedQuote = i + 1 < line.length() && line.charAt(i + 1) == '"';
                    if (isEscapedQuote) {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    result.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}