package main;

import javax.swing.*;

import ui.Login;
import util.SystemLookAndFeel;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SystemLookAndFeel.apply();
            } catch (Exception ignored) {
                // If FlatLaf isn't available, fall back silently to default LAF.
            }

            JFrame f = new JFrame("Smart Notes App");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1100, 720);
            f.setLocationRelativeTo(null);
            f.setContentPane(new Login(f));
            f.setVisible(true);
        });
    }
}

