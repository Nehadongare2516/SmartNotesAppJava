package ui.components;

import javax.swing.*;
import java.awt.*;

public class ShadowPanel extends JPanel {
    private final Color bg;

    public ShadowPanel(Color bg) {
        this.bg = bg;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 18;
        // Shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, arc, arc);

        // Panel
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth() - 16, getHeight() - 16, arc, arc);
        g2.dispose();

        super.paintComponent(g);
    }
}

