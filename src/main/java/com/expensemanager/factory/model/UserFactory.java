package com.expensemanager.factory.model;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.user.User;
import com.expensemanager.utils.PasswordUtils;

/** Lớp khởi tạo đối tượng User. */
public class UserFactory {
    private static final int MIN_PASSWORD_LENGTH = 8;

    /** Không cho phép khởi tạo đối tượng bên ngoài. */
    private UserFactory() {}

    /** Tạo mới một User cho luồng đăng ký. */
    public static User createUser(String id, String username, String rawPassword, String email, int userId) {

        /** Kiểm tra độ mạnh của mật khẩu. */
        // vadidatePasswordStrength(rawPassword);

        if (rawPassword == null || rawPassword.trim().isBlank()) {
            throw new EmptyFieldException(FieldType.PASSWORD);
        }

        /** Sinh salt và băm mật khẩu. */
        String salt = PasswordUtils.generateSalt();
        String passwordHash = PasswordUtils.hash(rawPassword, salt);

        return new User(id, username.trim(), passwordHash, salt, email, userId);
    }

    /** Khởi tạo đối tượng từ dữ liệu đã được lưu. */
    public static User reconstructUser(String id, String username, String passwordHash, String salt, String email, int userId) {
        return new User(id, username, passwordHash, salt, email, userId);
    }
}
