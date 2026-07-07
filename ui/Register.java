package ui;

import dao.UserDAO;
import ui.components.RoundedButton;
import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;

public class Register extends JPanel {
    private final JFrame host;

    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();

    public Register(JFrame host) {
        this.host = host;
        setLayout(new BorderLayout());
        setBackground(UiStyles.BG);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setForeground(UiStyles.TEXT);
        title.setFont(UiStyles.font(24, Font.BOLD));

        usernameField.setFont(UiStyles.font(14, Font.PLAIN));
        usernameField.setBackground(UiStyles.PANEL2);
        usernameField.setForeground(UiStyles.TEXT);
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        emailField.setFont(UiStyles.font(14, Font.PLAIN));
        emailField.setBackground(UiStyles.PANEL2);
        emailField.setForeground(UiStyles.TEXT);
        emailField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        passwordField.setFont(UiStyles.font(14, Font.PLAIN));
        passwordField.setBackground(UiStyles.PANEL2);
        passwordField.setForeground(UiStyles.TEXT);
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        confirmField.setFont(UiStyles.font(14, Font.PLAIN));
        confirmField.setBackground(UiStyles.PANEL2);
        confirmField.setForeground(UiStyles.TEXT);
        confirmField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(new Color(255, 120, 120));
        errorLabel.setFont(UiStyles.font(13, Font.BOLD));

        RoundedButton createBtn = new RoundedButton("Create", UiStyles.SUCCESS, Color.WHITE);
        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);

        gc.gridx = 0; gc.gridy = 0;
        content.add(title, gc);

        gc.gridx = 0; gc.gridy = 1;
        JLabel uLbl = new JLabel("Username");
        uLbl.setForeground(UiStyles.MUTED);
        content.add(uLbl, gc);
        gc.gridx = 0; gc.gridy = 2;
        usernameField.setPreferredSize(new Dimension(340, 44));
        content.add(usernameField, gc);

        gc.gridx = 0; gc.gridy = 3;
        JLabel eLbl = new JLabel("Email");
        eLbl.setForeground(UiStyles.MUTED);
        content.add(eLbl, gc);
        gc.gridx = 0; gc.gridy = 4;
        emailField.setPreferredSize(new Dimension(340, 44));
        content.add(emailField, gc);

        gc.gridx = 0; gc.gridy = 5;
        JLabel pLbl = new JLabel("Password");
        pLbl.setForeground(UiStyles.MUTED);
        content.add(pLbl, gc);
        gc.gridx = 0; gc.gridy = 6;
        passwordField.setPreferredSize(new Dimension(340, 44));
        content.add(passwordField, gc);

        gc.gridx = 0; gc.gridy = 7;
        JLabel cLbl = new JLabel("Confirm Password");
        cLbl.setForeground(UiStyles.MUTED);
        content.add(cLbl, gc);
        gc.gridx = 0; gc.gridy = 8;
        confirmField.setPreferredSize(new Dimension(340, 44));
        content.add(confirmField, gc);

        gc.gridx = 0; gc.gridy = 9;
        content.add(errorLabel, gc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(createBtn);
        btnRow.add(backBtn);

        gc.gridx = 0; gc.gridy = 10;
        content.add(btnRow, gc);

        add(content, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            host.setContentPane(new Login(host));
            host.revalidate();
            host.repaint();
        });

        createBtn.addActionListener(e -> {
            errorLabel.setText("");

            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.length() < 3) {
                errorLabel.setText("Username must be at least 3 characters.");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                errorLabel.setText("Please enter a valid email.");
                return;
            }
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters.");
                return;
            }
            if (!password.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }

            try {
                UserDAO dao = new UserDAO();
                if (dao.findByUsername(username) != null) {
                    errorLabel.setText("Username already exists.");
                    return;
                }
                if (dao.findByEmail(email) != null) {
                    errorLabel.setText("Email already exists.");
                    return;
                }

                long userId = dao.createUser(username, email, password);
                host.setContentPane(new Dashboard(host, userId));
                host.revalidate();
                host.repaint();
            } catch (Exception ex) {
                errorLabel.setText("Registration failed: " + ex.getMessage());
            }
        });
    }
}

