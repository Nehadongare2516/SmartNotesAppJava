package dao;

import model.User;
import storage.LocalDB;
import util.PasswordUtil;

import java.sql.SQLException;

/**
 * JSON/file-based DAO (no MySQL / no SQL).
 *
 * Methods still declare SQLException to avoid changing the UI code too much.
 */
public class UserDAO {

    public long createUser(String username, String email, String password) throws SQLException {
        return LocalDB.createUser(new User(0, username, email, PasswordUtil.hash(password)));
    }

    public User findByUsername(String username) throws SQLException {
        return LocalDB.findByUsername(username);
    }

    public User findByEmail(String email) throws SQLException {
        return LocalDB.findByEmail(email);
    }

    public boolean validateLogin(String username, String password) throws SQLException {
        User u = findByUsername(username);
        if (u == null) return false;
        return u.getPasswordHash() != null && u.getPasswordHash().equals(PasswordUtil.hash(password));
    }

    public boolean changePassword(long userId, String newPassword) throws SQLException {
        return LocalDB.changePassword(userId, PasswordUtil.hash(newPassword));
    }
}



