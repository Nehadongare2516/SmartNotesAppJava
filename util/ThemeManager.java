package util;

import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;

/**
 * Simple dark-mode toggle. For now, app is dark-themed by default.
 * We switch UI colors used by UiStyles.
 */
public final class ThemeManager {
    private static boolean dark = true;

    private ThemeManager() {}

    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean value) {
        dark = value;
        // UiStyles currently has constants; we keep this for future enhancement.
        // For now, we just trigger Swing LAF refresh.
        SwingUtilities.invokeLater(() -> {
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        });
    }

    public static void toggleDark() {
        setDark(!dark);
        JOptionPane.showMessageDialog(null, dark ? "Dark mode enabled" : "Light mode enabled");
    }

    public static Color bg() {
        return dark ? UiStyles.BG : new Color(245, 246, 250);
    }

    public static Color panel() {
        return dark ? UiStyles.PANEL : new Color(255, 255, 255);
    }

    public static Color panel2() {
        return dark ? UiStyles.PANEL2 : new Color(240, 243, 250);
    }

    public static Color text() {
        return dark ? UiStyles.TEXT : new Color(25, 30, 35);
    }

    public static Color muted() {
        return dark ? UiStyles.MUTED : new Color(95, 100, 110);
    }

    public static Color accent() {
        return UiStyles.ACCENT;
    }
}

