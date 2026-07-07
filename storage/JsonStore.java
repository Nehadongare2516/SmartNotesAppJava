package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Minimal local JSON-file persistence helper.
 *
 * NOTE: This project intentionally avoids external JSON libraries.
 * Data is stored as a very small JSON array/object format created by us.
 */
public final class JsonStore {
    private static final Path DATA_DIR = Paths.get("data");

    private JsonStore() {}

    public static Path resolve(String fileName) {
        return DATA_DIR.resolve(fileName);
    }

    public static void ensureDataDir() {
        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory: " + DATA_DIR, e);
        }
    }

    public static String readOrEmpty(String fileName) {
        ensureDataDir();
        Path p = resolve(fileName);
        try {
            if (!Files.exists(p)) return "";
            return Files.readString(p);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read: " + p, e);
        }
    }

    public static void write(String fileName, String content) {
        ensureDataDir();
        Path p = resolve(fileName);
        try {
            Files.writeString(p, content == null ? "" : content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write: " + p, e);
        }
    }
}

