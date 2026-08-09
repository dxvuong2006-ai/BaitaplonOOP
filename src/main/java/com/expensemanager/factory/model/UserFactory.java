package com.expensemanager.factory.model;

import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.user.User;
import com.expensemanager.utils.PasswordUtils;

/** Lớp khởi tạo đối tượng User. */
public class UserFactory {
    private static final int MIN_PASSWORD_LENGTH = 8;

    /** Không cho phép khởi tạo đối tượng bên ngoài. */
    private UserFactory() {}

    /** Tạo mới một User cho luồng đăng ký. */
    public static User createUser(String id, String username, String rawPassword, String email) {

        /** Kiểm tra độ mạnh cảu mật khẩu. */
        // vadidatePasswordStrength(rawPassword);

        /** Sinh salt và băm mật khẩu. */
        String salt = PasswordUtils.generateSalt();
        String passwordHash = PasswordUtils.hash(rawPassword, salt);

        return new User(id, username.trim(), passwordHash, salt, email);
    }

    /** Khởi tạo đối tượng từ dữ liệu đã được lưu. */
    public static User reconstructUser(String id, String username, String passwordHash, String salt, String email) {
        return new User(id, username, passwordHash, salt, email);
    }
}
