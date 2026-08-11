package com.expensemanager.factory.model;

import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.user.User;
import com.expensemanager.utils.PasswordUtils;

/** Lớp khởi tạo đối tượng {@link User}. */
public final class UserFactory {

    private static final int MIN_PASSWORD_LENGTH = 8;

    /** Không cho phép khởi tạo đối tượng bên ngoài. */
    private UserFactory() {}

    /**
     * Tạo mới một {@link User} cho luồng đăng ký.
     *
     * @param id mã định danh người dùng
     * @param username tên đăng nhập
     * @param rawPassword mật khẩu chưa được băm
     * @param email địa chỉ email người dùng
     * @param userId mã số người dùng
     * @return đối tượng {@link User} vừa được tạo
     * @throws EmptyFieldException nếu {@code rawPassword} rỗng hoặc null
     */
    public static User createUser(
            String id, String username, String rawPassword, String email, int userId) {
        // Kiểm tra độ mạnh của mật khẩu.
        // vadidatePasswordStrength(rawPassword);

        if (rawPassword == null || rawPassword.trim().isBlank()) {
            throw new EmptyFieldException(FieldType.PASSWORD);
        }

        // Sinh salt và băm mật khẩu.
        String salt = PasswordUtils.generateSalt();
        String passwordHash = PasswordUtils.hash(rawPassword, salt);

        return new User(id, username.trim(), passwordHash, salt, email, userId);
    }

    /**
     * Khởi tạo đối tượng {@link User} từ dữ liệu đã được lưu (ví dụ: từ database).
     *
     * @param id mã định danh người dùng
     * @param username tên đăng nhập
     * @param passwordHash mật khẩu đã được băm
     * @param salt salt dùng để băm mật khẩu
     * @param email địa chỉ email người dùng
     * @param userId mã số người dùng
     * @return đối tượng {@link User} được khôi phục
     */
    public static User reconstructUser(
            String id,
            String username,
            String passwordHash,
            String salt,
            String email,
            int userId) {
        return new User(id, username, passwordHash, salt, email, userId);
    }
}