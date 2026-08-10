package com.expensemanager.model.User;

import java.util.Objects;

/**
 * Lớp đại diện cho một tài khoản người dùng.
 * KHÔNG bao giờ lưu mật khẩu dạng plain text — chỉ giữ passwordHash + salt.
 */
public class User {

    private String id;
    private String username;
    private String passwordHash;
    private String salt;
    private String email;

    /** Khởi tạo một tài khoản người dùng. */
    public User(String id, String username, String passwordHash, String salt, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username không được để trống.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash không được để trống.");
        }
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.email = email;
    }

    /** Lấy định danh của người dùng. */
    public String getId() {
        return id;
    }

    /** Gán lại định danh cho người dùng. */
    public void setId(String id) {
        this.id = id;
    }

    /** Lấy tên đăng nhập. */
    public String getUsername() {
        return username;
    }

    /** Gán lại tên đăng nhập. */
    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username không được để trống.");
        }
        this.username = username;
    }

    /** Lấy mật khẩu đã băm (hash). */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** Gán lại mật khẩu đã băm. */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** Lấy salt dùng khi băm mật khẩu. */
    public String getSalt() {
        return salt;
    }

    /** Gán lại salt dùng khi băm mật khẩu. */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /** Lấy email của người dùng. */
    public String getEmail() {
        return email;
    }

    /** Gán lại email cho người dùng. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** So sánh hai tài khoản có cùng định danh (id) hay không. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id.equals(((User) o).id);
    }

    /** Sinh mã băm dựa trên id, khớp với logic của {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** Biểu diễn tài khoản dưới dạng chuỗi dễ đọc, phục vụ debug/log. */
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}