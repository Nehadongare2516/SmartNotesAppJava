package storage;

import model.Category;
import model.Note;
import model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Very small local “DB” using plain text/JSON-like storage.
 *
 * To keep this project minimal (no external JSON libs), we store data as newline-delimited records.
 * This is not a general JSON parser; it's just enough for this demo app.
 */
public final class LocalDB {

    private static final String USERS_FILE = "users.jsonl";
    private static final String NOTES_FILE = "notes.jsonl";
    private static final String CATEGORIES_FILE = "categories.jsonl";

    private static final Map<Long, User> users = new LinkedHashMap<>();
    private static final Map<Long, Category> categories = new LinkedHashMap<>();
    private static final Map<Long, Note> notes = new LinkedHashMap<>();

    private static long nextUserId = 1;
    private static long nextCategoryId = 1;
    private static long nextNoteId = 1;

    private static boolean loaded = false;

    private LocalDB() {}

    private static void loadIfNeeded() {
        if (loaded) return;
        loaded = true;
        JsonStore.ensureDataDir();

        readAll(USERS_FILE, lines -> {
            for (String line : lines) {
                if (line.isBlank()) continue;
                // userId|username|email|passwordHash
                String[] p = line.split("\\|", -1);
                if (p.length < 4) continue;
                long id = Long.parseLong(p[0]);
                String username = unescape(p[1]);
                String email = unescape(p[2]);
                String passwordHash = unescape(p[3]);
                users.put(id, new User(id, username, email, passwordHash));
                nextUserId = Math.max(nextUserId, id + 1);
            }
        });

        readAll(CATEGORIES_FILE, lines -> {
            for (String line : lines) {
                if (line.isBlank()) continue;
                // categoryId|userId|name
                String[] p = line.split("\\|", -1);
                if (p.length < 3) continue;
                long id = Long.parseLong(p[0]);
                long userId = Long.parseLong(p[1]);
                String name = unescape(p[2]);
                categories.put(id, new Category(id, userId, name));
                nextCategoryId = Math.max(nextCategoryId, id + 1);
            }
        });

        readAll(NOTES_FILE, lines -> {
            for (String line : lines) {
                if (line.isBlank()) continue;
                // noteId|userId|categoryIdOr-1|title|content|pinned|favorite|archived
                String[] p = line.split("\\|", -1);
                if (p.length < 8) continue;
                long id = Long.parseLong(p[0]);
                long userId = Long.parseLong(p[1]);
                long cat = Long.parseLong(p[2]);
                Long categoryId = (cat == -1) ? null : cat;
                String title = unescape(p[3]);
                String content = unescape(p[4]);
                boolean pinned = Boolean.parseBoolean(p[5]);
                boolean favorite = Boolean.parseBoolean(p[6]);
                boolean archived = Boolean.parseBoolean(p[7]);
                Note n = new Note();
                n.setId(id);
                n.setUserId(userId);
                n.setCategoryId(categoryId);
                n.setTitle(title);
                n.setContent(content);
                n.setPinned(pinned);
                n.setFavorite(favorite);
                n.setArchived(archived);
                notes.put(id, n);
                nextNoteId = Math.max(nextNoteId, id + 1);
            }
        });
    }

    private static void readAll(String fileName, java.util.function.Consumer<List<String>> consumer) {
        String raw = JsonStore.readOrEmpty(fileName);
        if (raw.isBlank()) return;
        List<String> lines = Arrays.asList(raw.split("\\R", -1));
        consumer.accept(lines);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("|", "\\p");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\p", "|")
                .replace("\\\\", "\\");
    }

    private static void persistAll() {
        // rewrite all files (small app)
        persist(USERS_FILE, () -> users.values(), u ->
                u.getId() + "|" + escape(u.getUsername()) + "|" + escape(u.getEmail()) + "|" + escape(u.getPasswordHash())
        );

        persist(CATEGORIES_FILE, () -> categories.values(), c ->
                c.getId() + "|" + c.getUserId() + "|" + escape(c.getName())
        );

        persist(NOTES_FILE, () -> notes.values(), n -> {
            Long cat = n.getCategoryId();
            long catId = (cat == null) ? -1 : cat;
            return n.getId() + "|" + n.getUserId() + "|" + catId + "|" +
                    escape(n.getTitle()) + "|" + escape(n.getContent()) + "|" +
                    n.isPinned() + "|" + n.isFavorite() + "|" + n.isArchived();
        });
    }

    private static <T> void persist(String fileName, java.util.function.Supplier<Collection<T>> supplier,
                                     java.util.function.Function<T, String> toLine) {
        JsonStore.ensureDataDir();
        Path p = JsonStore.resolve(fileName);
        List<String> lines = new ArrayList<>();
        for (T t : supplier.get()) {
            lines.add(toLine.apply(t));
        }
        try {
            Files.writeString(p, String.join(System.lineSeparator(), lines));
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist: " + p, e);
        }
    }

    // USERS
    public static User findByUsername(String username) {
        loadIfNeeded();
        for (User u : users.values()) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public static User findByEmail(String email) {
        loadIfNeeded();
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) return u;
        }
        return null;
    }

    public static long createUser(User u) {
        loadIfNeeded();
        long id = nextUserId++;
        User stored = new User(id, u.getUsername(), u.getEmail(), u.getPasswordHash());
        users.put(id, stored);
        persistAll();
        return id;
    }

    public static User findById(long id) {
        loadIfNeeded();
        return users.get(id);
    }

    public static boolean changePassword(long userId, String newHash) {
        loadIfNeeded();
        User u = users.get(userId);
        if (u == null) return false;
        User updated = new User(u.getId(), u.getUsername(), u.getEmail(), newHash);
        users.put(userId, updated);
        persistAll();
        return true;
    }

    // CATEGORIES
    public static long createCategory(long userId, String name) {
        loadIfNeeded();
        long id = nextCategoryId++;
        categories.put(id, new Category(id, userId, name));
        persistAll();
        return id;
    }

    public static List<Category> listCategories(long userId) {
        loadIfNeeded();
        List<Category> out = new ArrayList<>();
        for (Category c : categories.values()) {
            if (c.getUserId() == userId) out.add(c);
        }
        out.sort(Comparator.comparing(Category::getName));
        return out;
    }

    public static Long findCategoryIdByName(long userId, String name) {
        loadIfNeeded();
        for (Category c : categories.values()) {
            if (c.getUserId() == userId && c.getName().equals(name)) return c.getId();
        }
        return null;
    }

    // NOTES
    public static long createNote(long userId, Long categoryId, String title, String content) {
        loadIfNeeded();
        long id = nextNoteId++;
        Note n = new Note();
        n.setId(id);
        n.setUserId(userId);
        n.setCategoryId(categoryId);
        n.setTitle(title);
        n.setContent(content);
        n.setPinned(false);
        n.setFavorite(false);
        n.setArchived(false);
        notes.put(id, n);
        persistAll();
        return id;
    }

    public static Note findById(long userId, long noteId) {
        loadIfNeeded();
        Note n = notes.get(noteId);
        if (n == null) return null;
        if (n.getUserId() != userId) return null;
        return cloneNote(n);
    }

    private static Note cloneNote(Note n) {
        Note c = new Note();
        c.setId(n.getId());
        c.setUserId(n.getUserId());
        c.setCategoryId(n.getCategoryId());
        c.setTitle(n.getTitle());
        c.setContent(n.getContent());
        c.setPinned(n.isPinned());
        c.setFavorite(n.isFavorite());
        c.setArchived(n.isArchived());
        c.setCreatedAt(n.getCreatedAt());
        c.setUpdatedAt(n.getUpdatedAt());
        return c;
    }

    public static List<Note> listNotes(long userId, boolean archived) {
        loadIfNeeded();
        List<Note> out = new ArrayList<>();
        for (Note n : notes.values()) {
            if (n.getUserId() == userId && n.isArchived() == archived) out.add(cloneNote(n));
        }
        out.sort(Comparator.comparing(Note::isPinned).reversed()
                .thenComparing(Note::isFavorite).reversed()
                .thenComparing(Note::getId).reversed());
        return out;
    }

    public static List<Note> searchNotes(long userId, String query, Long categoryId, boolean archived) {
        loadIfNeeded();
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);
        List<Note> out = new ArrayList<>();
        for (Note n : notes.values()) {
            if (n.getUserId() != userId) continue;
            if (n.isArchived() != archived) continue;
            if (categoryId != null && !Objects.equals(n.getCategoryId(), categoryId)) continue;
            String t = n.getTitle() == null ? "" : n.getTitle().toLowerCase(Locale.ROOT);
            String c = n.getContent() == null ? "" : n.getContent().toLowerCase(Locale.ROOT);
            if (t.contains(q) || c.contains(q)) out.add(cloneNote(n));
        }
        out.sort(Comparator.comparing(Note::isPinned).reversed()
                .thenComparing(Note::isFavorite).reversed()
                .thenComparing(Note::getId).reversed());
        return out;
    }

    public static boolean updateNote(long userId, long noteId, Long categoryId, String title, String content) {
        loadIfNeeded();
        Note n = notes.get(noteId);
        if (n == null || n.getUserId() != userId) return false;
        n.setCategoryId(categoryId);
        n.setTitle(title);
        n.setContent(content);
        notes.put(noteId, n);
        persistAll();
        return true;
    }

    public static boolean deleteNote(long userId, long noteId) {
        loadIfNeeded();
        Note n = notes.get(noteId);
        if (n == null || n.getUserId() != userId) return false;
        notes.remove(noteId);
        persistAll();
        return true;
    }

    public static boolean setPinned(long userId, long noteId, boolean pinned) {
        loadIfNeeded();
        Note n = notes.get(noteId);
        if (n == null || n.getUserId() != userId) return false;
        n.setPinned(pinned);
        persistAll();
        return true;
    }

    public static boolean setFavorite(long userId, long noteId, boolean favorite) {
        loadIfNeeded();
        Note n = notes.get(noteId);
        if (n == null || n.getUserId() != userId) return false;
        n.setFavorite(favorite);
        persistAll();
        return true;
    }

    public static boolean archive(long userId, long noteId, boolean archived) {
        loadIfNeeded();
        Note n = notes.get(noteId);
        if (n == null || n.getUserId() != userId) return false;
        n.setArchived(archived);
        persistAll();
        return true;
    }
}

