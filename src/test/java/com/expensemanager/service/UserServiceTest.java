package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.factory.storage.UserStorageFactory;
import com.expensemanager.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test xác thực và quản lý tài khoản người dùng")
class UserServiceTest {

    private UserService userService;
    private UserStorageFactory storageFactory;

    @BeforeEach
    void setUp() {
        storageFactory = mock(UserStorageFactory.class);
        when(storageFactory.load(any())).thenReturn(List.of());
        userService = new UserService(storageFactory);
    }

    @Test
    @DisplayName("Đăng ký tài khoản trùng tên đăng nhập ném DuplicateEntityException")
    void testRegisterDuplicateUsernameThrowsException() {
        userService.register("U1", "john_doe", "password123", "john@gmail.com");

        assertThrows(DuplicateEntityException.class, () -> {
            userService.register("U2", "john_doe", "newpass456", "john2@gmail.com");
        }, "Phải chặn việc đăng ký trùng username john_doe");
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi sai mật khẩu")
    void testLoginWithWrongPasswordReturnsNull() {
        userService.register("U1", "alice", "correctPassword", "alice@gmail.com");

        User result = userService.login("alice", "wrongPassword");

        assertNull(result, "Đăng nhập sai mật khẩu phải trả về null");
        assertNull(userService.getCurrentUser(), "Current user không được thiết lập");
    }
}
