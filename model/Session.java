package model;

/**
 * Simple in-memory session (current logged-in user).
 * Since this is a desktop app, we only keep state in memory.
 */
public class Session {
    private static long userId = -1;

    private Session() {}

    public static void login(long id) {
        userId = id;
    }

    public static void logout() {
        userId = -1;
    }

    public static boolean isLoggedIn() {
        return userId >= 0;
    }

    public static long getUserId() {
        return userId;
    }
}

