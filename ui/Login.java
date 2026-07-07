package ui;

import dao.UserDAO;
import ui.components.RoundedButton;
import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {
    private final JFrame host;
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public Login(JFrame host) {
        this.host = host;
        setLayout(new BorderLayout());
        setBackground(UiStyles.BG);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Smart Notes");
        title.setForeground(UiStyles.TEXT);
        title.setFont(UiStyles.font(28, Font.BOLD));

        JLabel subtitle = new JLabel("Login to continue");
        subtitle.setForeground(UiStyles.MUTED);
        subtitle.setFont(UiStyles.font(14, Font.PLAIN));

        usernameField.setFont(UiStyles.font(14, Font.PLAIN));
        usernameField.setBackground(UiStyles.PANEL2);
        usernameField.setForeground(UiStyles.TEXT);
        usernameField.setCaretColor(UiStyles.TEXT);
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        passwordField.setFont(UiStyles.font(14, Font.PLAIN));
        passwordField.setBackground(UiStyles.PANEL2);
        passwordField.setForeground(UiStyles.TEXT);
        passwordField.setCaretColor(UiStyles.TEXT);
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        RoundedButton loginBtn = new RoundedButton("Login", UiStyles.ACCENT, Color.WHITE);
        RoundedButton registerBtn = new RoundedButton("Register", UiStyles.PANEL2, UiStyles.TEXT);

        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(new Color(255, 120, 120));
        errorLabel.setFont(UiStyles.font(13, Font.BOLD));

        gc.gridx = 0; gc.gridy = 0;
        content.add(title, gc);
        gc.gridx = 0; gc.gridy = 1;
        content.add(subtitle, gc);

        gc.gridx = 0; gc.gridy = 2;
        JLabel uLbl = new JLabel("Username");
        uLbl.setForeground(UiStyles.MUTED);
        content.add(uLbl, gc);

        gc.gridx = 0; gc.gridy = 3;
        usernameField.setPreferredSize(new Dimension(320, 44));
        content.add(usernameField, gc);

        gc.gridx = 0; gc.gridy = 4;
        JLabel pLbl = new JLabel("Password");
        pLbl.setForeground(UiStyles.MUTED);
        content.add(pLbl, gc);

        gc.gridx = 0; gc.gridy = 5;
        passwordField.setPreferredSize(new Dimension(320, 44));
        content.add(passwordField, gc);

        gc.gridx = 0; gc.gridy = 6;
        content.add(errorLabel, gc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(loginBtn);
        btnRow.add(registerBtn);

        gc.gridx = 0; gc.gridy = 7;
        content.add(btnRow, gc);

        add(content, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            errorLabel.setText("");
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and password are required.");
                return;
            }

            try {
                UserDAO dao = new UserDAO();
                if (!dao.validateLogin(username, password)) {
                    errorLabel.setText("Invalid username or password.");
                    return;
                }

                // Get user id for session.
                var user = dao.findByUsername(username);
                model.Session.login(user.getId());
                host.setContentPane(new Dashboard(host, user.getId()));

                host.revalidate();
                host.repaint();
            } catch (Exception ex2) {
                errorLabel.setText("Login failed: " + ex2.getMessage());
            }
        });

        registerBtn.addActionListener(e -> {
            host.setContentPane(new Register(host));
            host.revalidate();
            host.repaint();
        });
    }
}

