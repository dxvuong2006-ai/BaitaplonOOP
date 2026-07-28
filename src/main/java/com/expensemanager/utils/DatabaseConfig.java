package com.expensemanager.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    // Tên file database sẽ được lưu thẳng vào thư mục dự án của bạn
    private static final String URL = "jdbc:sqlite:expense_manager.db";

    // Hàm lấy kết nối (Các class Repository sẽ gọi hàm này)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Hàm tự động tạo bảng nếu chưa có
    public static void initializeDatabase() {
        // Câu lệnh SQL tạo các bảng dựa trên Model của bạn
        String createWalletTable = "CREATE TABLE IF NOT EXISTS wallets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "balance REAL DEFAULT 0.0" +
                ");";

        String createCategoryTable = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "type TEXT NOT NULL" + // 'INCOME' hoặc 'EXPENSE'
                ");";

        String createBudgetTable = "CREATE TABLE IF NOT EXISTS budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER, " +
                "limit_amount REAL NOT NULL, " +
                "start_date TEXT, " +
                "end_date TEXT, " +
                "FOREIGN KEY(category_id) REFERENCES categories(id)" +
                ");";

        // Thực thi các câu lệnh SQL trên
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createWalletTable);
            stmt.execute(createCategoryTable);
            stmt.execute(createBudgetTable);
            System.out.println("Đã khởi tạo Database và các bảng thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo database: " + e.getMessage());
        }
    }
}