package dao;

import model.Note;
import storage.LocalDB;

import java.sql.SQLException;
import java.util.List;

/**
 * JSON/file-based DAO (no MySQL / no SQL).
 */
public class NoteDAO {

    public long createNote(long userId, Long categoryId, String title, String content) throws SQLException {
        return LocalDB.createNote(userId, categoryId, title, content);
    }

    public Note findById(long userId, long noteId) throws SQLException {
        return LocalDB.findById(userId, noteId);
    }

    public List<Note> listNotes(long userId, boolean archived) throws SQLException {
        return LocalDB.listNotes(userId, archived);
    }

    public List<Note> searchNotes(long userId, String query, Long categoryId, boolean archived) throws SQLException {
        return LocalDB.searchNotes(userId, query, categoryId, archived);
    }

    public boolean updateNote(long userId, long noteId, Long categoryId, String title, String content) throws SQLException {
        return LocalDB.updateNote(userId, noteId, categoryId, title, content);
    }

    public boolean deleteNote(long userId, long noteId) throws SQLException {
        return LocalDB.deleteNote(userId, noteId);
    }

    public boolean setPinned(long userId, long noteId, boolean pinned) throws SQLException {
        return LocalDB.setPinned(userId, noteId, pinned);
    }

    public boolean setFavorite(long userId, long noteId, boolean favorite) throws SQLException {
        return LocalDB.setFavorite(userId, noteId, favorite);
    }

    public boolean archive(long userId, long noteId, boolean archived) throws SQLException {
        return LocalDB.archive(userId, noteId, archived);
    }
}



