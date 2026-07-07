package ui.components;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private Color bg;
    private Color fg;
    private int radius = 16;

    public RoundedButton(String text, Color bg, Color fg) {
        super(text);
        this.bg = bg;
        this.fg = fg;
        setForeground(fg);
        setBackground(bg);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(UiStyles.font(14, Font.BOLD));
        setPreferredSize(new Dimension(120, 40));
    }

    public RoundedButton(String text) {
        this(text, UiStyles.ACCENT, Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border (clean modern look).
    }
}


