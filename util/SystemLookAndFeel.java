package util;

import javax.swing.*;

public final class SystemLookAndFeel {
    private SystemLookAndFeel() {}

    public static void apply() {
        // Optional: FlatLaf if present on classpath.
        // Project should still run without it.
        try {
            Class<?> cls = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            LookAndFeel laf = (LookAndFeel) cls.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
            return;
        } catch (Throwable ignored) {
            // Fall back to default.
        }
    }
}

