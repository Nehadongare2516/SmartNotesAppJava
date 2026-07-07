package database;

import java.sql.*;

public final class DatabaseConnection {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "smartnotes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // CHANGE HERE if needed

    private static final String JDBC_URL_NO_DB = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {

        try {
            // Connector/J 8+ (most common)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            // Some older packaging uses the legacy driver class name
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e2) {
                throw new SQLException(
                        "MySQL JDBC Driver not found. Add mysql-connector-j (Connector/J 9.3.0) jar to lib/. " +
                                "Expected class: com.mysql.cj.jdbc.Driver",
                        e2
                );
            }
        }


        // NOTE: SQL initialization removed as per current requirement.
        // If tables are required, create them manually in MySQL.
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }


    private static void ensureDatabaseAndTables() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL_NO_DB, DB_USER, DB_PASSWORD);
             Statement st = conn.createStatement()) {

            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             Statement st = conn.createStatement()) {

            // Users
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Categories
            st.executeUpdate("CREATE TABLE IF NOT EXISTS categories (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id BIGINT NOT NULL," +
                    "name VARCHAR(60) NOT NULL," +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY uq_user_category (user_id, name)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")");

            // Notes
            st.executeUpdate("CREATE TABLE IF NOT EXISTS notes (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id BIGINT NOT NULL," +
                    "category_id BIGINT NULL," +
                    "title VARCHAR(120) NOT NULL," +
                    "content TEXT NOT NULL," +
                    "is_pinned BOOLEAN NOT NULL DEFAULT FALSE," +
                    "is_favorite BOOLEAN NOT NULL DEFAULT FALSE," +
                    "is_archived BOOLEAN NOT NULL DEFAULT FALSE," +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL" +
                    ")");

            // Sample data (only if empty)
            try (Statement s2 = conn.createStatement();
                 ResultSet rs = s2.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Default sample user is created by app logic (no hashing here).
                    // Keeping DB init deterministic: insert a minimal sample category for future.
                }
            }
        }
    }
}

