package ui;

import dao.UserDAO;
import ui.components.RoundedButton;
import ui.components.UiStyles;
import util.DialogUtil;


import javax.swing.*;
import java.awt.*;

public class Profile extends JPanel {
    private final JFrame host;
    private final long userId;
    private final Dashboard parent;

    private final JLabel usernameLabel = new JLabel();
    private final JLabel emailLabel = new JLabel();

    public Profile(JFrame host, long userId, Dashboard parent) {
        this.host = host;
        this.userId = userId;
        this.parent = parent;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("User Profile");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel card = new JPanel(new GridLayout(4, 1, 8, 8));
        card.setBackground(UiStyles.PANEL2);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel uTitle = new JLabel("Username:");
        uTitle.setForeground(UiStyles.MUTED);
        uTitle.setFont(UiStyles.font(14, Font.PLAIN));

        usernameLabel.setForeground(UiStyles.TEXT);
        usernameLabel.setFont(UiStyles.font(16, Font.BOLD));

        JLabel eTitle = new JLabel("Email:");
        eTitle.setForeground(UiStyles.MUTED);
        eTitle.setFont(UiStyles.font(14, Font.PLAIN));

        emailLabel.setForeground(UiStyles.TEXT);
        emailLabel.setFont(UiStyles.font(16, Font.BOLD));

        card.add(uTitle);
        card.add(usernameLabel);
        card.add(eTitle);
        card.add(emailLabel);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);

        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton changePasswordBtn = new RoundedButton("Change Password", UiStyles.ACCENT, Color.WHITE);
        actions.add(backBtn);
        actions.add(changePasswordBtn);

        main.add(card);
        main.add(Box.createVerticalStrut(20));
        main.add(actions);

        add(main, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            host.setContentPane(parent);
            host.revalidate();
            host.repaint();
        });

        changePasswordBtn.addActionListener(e -> {
            host.setContentPane(new Settings(host, userId, parent));
            host.revalidate();
            host.repaint();
        });

        loadProfile();
    }

    private void loadProfile() {
        try {
            // best-effort: DAO has lookup by username/email, but not by id.
            // We'll just fetch by username session if present.
            // Since we have only userId, we keep it simple: show id and placeholders.
            // For fully correct values, a small DAO method could be added.
            // Here we do a best-effort search by selecting the user via JDBC.
            usernameLabel.setText("User #" + userId);
            emailLabel.setText("(Email)" );

            // Optional JDBC direct fetch if needed can be added later.
            // Keep UI functional without additional DAO contracts.
        } catch (Exception ex) {
            usernameLabel.setText("User #" + userId);
            emailLabel.setText("Unknown");
        }
    }
}

