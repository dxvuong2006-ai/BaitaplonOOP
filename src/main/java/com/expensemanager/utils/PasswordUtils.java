package com.expensemanager.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtils {

    private PasswordUtils() {
        throw new UnsupportedOperationException("Utility class không thể khởi tạo!");
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String rawPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashed = md.digest(rawPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new ExpenseManagerRuntimeWrap(e);
        }
    }

    public static boolean verify(String rawPassword, String salt, String expectedHash) {
        return hash(rawPassword, salt).equals(expectedHash);
    }

    // helper nội bộ, tránh checked exception leak ra ngoài
    private static class ExpenseManagerRuntimeWrap extends RuntimeException {
        ExpenseManagerRuntimeWrap(Throwable cause) { super(cause); }
    }
}