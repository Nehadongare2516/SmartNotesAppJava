package ui.components;

import java.awt.*;

public final class UiStyles {
    private UiStyles() {}

    public static final Color BG = new Color(18, 20, 24);
    public static final Color PANEL = new Color(28, 32, 40);
    public static final Color PANEL2 = new Color(35, 40, 51);
    public static final Color TEXT = new Color(235, 236, 240);
    public static final Color MUTED = new Color(150, 155, 165);
    public static final Color ACCENT = new Color(60, 180, 255);
    public static final Color DANGER = new Color(255, 80, 80);
    public static final Color SUCCESS = new Color(90, 200, 120);

    public static Font font(int size, int style) {
        return new Font("Segoe UI", style, size);
    }
}

