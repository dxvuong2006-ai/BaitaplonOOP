package com.expensemanager.utils;

/**
 * Chiến lược định dạng ngày kiểu Việt Nam: dd/MM/uuuu (ví dụ: 22/07/2026).
 * Mặc định cho hiển thị CLI và GUI.
 */
public final class SlashDateFormatStrategy extends DateFormatStrategy {

    /** Khởi tạo chiến lược định dạng dấu gạch chéo. */
    public SlashDateFormatStrategy() {
        super("dd/MM/uuuu");
    }
}
