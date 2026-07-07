package dao;

import model.Category;
import storage.LocalDB;

import java.sql.SQLException;
import java.util.List;

/**
 * JSON/file-based DAO (no MySQL / no SQL).
 */
public class CategoryDAO {

    public long createCategory(long userId, String name) throws SQLException {
        // Prevent duplicates by name (best-effort)
        Long existing = LocalDB.findCategoryIdByName(userId, name);
        if (existing != null) return existing;
        return LocalDB.createCategory(userId, name);
    }

    public List<Category> listCategories(long userId) throws SQLException {
        return LocalDB.listCategories(userId);
    }

    public Long findCategoryIdByName(long userId, String name) throws SQLException {
        return LocalDB.findCategoryIdByName(userId, name);
    }
}



