package util;

public final class ValidationUtil {
    private ValidationUtil() {}

    public static String requireNonEmpty(String v, String fieldName) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return v.trim();
    }

    public static void validateUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username is required.");
        if (username.trim().length() < 3) throw new IllegalArgumentException("Username must be at least 3 characters.");
    }

    public static void validateEmail(String email) {
        if (email == null) throw new IllegalArgumentException("Email is required.");
        String e = email.trim();
        if (!e.contains("@") || !e.contains(".")) {
            throw new IllegalArgumentException("Please enter a valid email.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null) throw new IllegalArgumentException("Password is required.");
        if (password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters.");
    }
}

