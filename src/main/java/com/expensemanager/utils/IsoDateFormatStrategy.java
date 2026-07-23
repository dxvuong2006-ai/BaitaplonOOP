package com.expensemanager.utils;

/**
 * Chiến lược định dạng ngày chuẩn ISO-8601: uuuu-MM-dd (ví dụ: 2026-07-22).
 * Sử dụng khi lưu trữ file CSV/JSON để sắp xếp chuỗi chính xác.
 */
public final class IsoDateFormatStrategy extends DateFormatStrategy {

    /** Khởi tạo chiến lược định dạng ISO. */
    public IsoDateFormatStrategy() {
        super("uuuu-MM-dd");
    }
}
