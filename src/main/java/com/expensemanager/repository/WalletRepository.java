package com.expensemanager.repository;

import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WalletRepository {

    // Nhận vào đối tượng wallet (chữ w viết thường)
    public void addWallet(Wallet w) {
        String sql = "INSERT INTO wallets(name, balance) VALUES(?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, w.getName());
            pstmt.setDouble(2, w.getBalance());

            pstmt.executeUpdate();
            System.out.println("Đã lưu ví: " + w.getName() + " thành công vào Database!");

        } catch (SQLException e) {
            System.out.println("Lỗi khi lưu ví: " + e.getMessage());
        }
    }
}