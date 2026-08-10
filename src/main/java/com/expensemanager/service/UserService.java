package com.expensemanager.service;

import com.expensemanager.exception.DuplicateEntityException;
import com.expensemanager.exception.EmptyFieldException;
import com.expensemanager.factory.model.UserFactory;
import com.expensemanager.factory.storage.UserStorageFactory;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.enums.FilePath;
import com.expensemanager.model.user.User;
import com.expensemanager.utils.PasswordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Lớp quản lý người dùng. */
public class UserService {
    private final List<User> users;
    private final UserStorageFactory storageFactory;
    private User currentUser;

    /** Khởi tạo. */
    public UserService(UserStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
        this.users = new ArrayList<>();
        load();
    }

    /** Tự tạo userId. */
    private int generateNextUserId() {
        int max = 0;
        for (User user : users) {
            if (user.getUserId() > max) {
                max = user.getUserId();
            }
        }
        return max + 1;
    }

    /** Tải dữ liệu người dùng. */
    public void load() {
        users.clear();
        users.addAll(storageFactory.load(FilePath.USER));
    }

    /** Lưu dữ liệu người dùng. */
    public void save() {
        storageFactory.save(FilePath.USER, users);
    }

    /** Đăng ký tài khoản. */
    public User register(String id, String username, String rawPassword, String email) {
        if (findByUsername(username) != null) {
            throw new DuplicateEntityException("Tên đăng nhập", username);
        }
        int userId = generateNextUserId();
        User user = UserFactory.createUser(id, username, rawPassword, email, userId);
        users.add(user);
        save();
        return user;
    }

    /** Đăng nhập tài khoản. */
    public User login(String username, String rawPassword) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        boolean valid = PasswordUtils.verify(rawPassword, user.getSalt(), user.getPasswordHash());
        if (!valid) {
            return null;
        }
        currentUser = user;
        return user;
    }

    /** Đăng xuất tài khoản. */
    public void logout() {
        currentUser = null;
    }

    /** Người dùng hiện tại. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** ID người dùng hiện tại. */
    public int getCurrentUserId() {
        if (currentUser == null) {
            throw new EmptyFieldException(FieldType.USERID);
        }
        return currentUser.getUserId();
    }

    /** Tìm tài khoản bằng tên. */
    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return user;
            }
        }
        return null;
    }

    /** Tìm tài khoản bằng ID. */
    public User findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    /** Trả về danh sách tài khoản. */
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }
}

