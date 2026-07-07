package ui;

import dao.CategoryDAO;
import dao.NoteDAO;
import model.Category;
import ui.components.RoundedButton;
import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AddNote extends JPanel {
    private final JFrame host;
    private final long userId;
    private final Dashboard parent;

    private final JTextField titleField = new JTextField();
    private final JTextArea contentArea = new JTextArea();
    private final JComboBox<String> categoryCombo = new JComboBox<>();

    public AddNote(JFrame host, long userId, Dashboard parent) {
        this.host = host;
        this.userId = userId;
        this.parent = parent;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Add Note");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        form.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        titleField.setBackground(UiStyles.PANEL2);
        titleField.setForeground(UiStyles.TEXT);
        titleField.setCaretColor(UiStyles.TEXT);
        titleField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        titleField.setFont(UiStyles.font(14, Font.PLAIN));

        contentArea.setBackground(UiStyles.PANEL2);
        contentArea.setForeground(UiStyles.TEXT);
        contentArea.setCaretColor(UiStyles.TEXT);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        contentArea.setFont(UiStyles.font(14, Font.PLAIN));

        categoryCombo.setBackground(UiStyles.PANEL2);
        categoryCombo.setForeground(UiStyles.TEXT);
        categoryCombo.setFont(UiStyles.font(14, Font.PLAIN));

        gc.gridx = 0; gc.gridy = 0;
        JLabel tLbl = new JLabel("Title");
        tLbl.setForeground(UiStyles.MUTED);
        form.add(tLbl, gc);

        gc.gridx = 0; gc.gridy = 1;
        titleField.setPreferredSize(new Dimension(420, 44));
        form.add(titleField, gc);

        gc.gridx = 0; gc.gridy = 2;
        JLabel cLbl = new JLabel("Category");
        cLbl.setForeground(UiStyles.MUTED);
        form.add(cLbl, gc);

        gc.gridx = 0; gc.gridy = 3;
        categoryCombo.setPreferredSize(new Dimension(420, 44));
        form.add(categoryCombo, gc);

        gc.gridx = 0; gc.gridy = 4;
        JLabel contentLbl = new JLabel("Content");
        contentLbl.setForeground(UiStyles.MUTED);
        form.add(contentLbl, gc);

        gc.gridx = 0; gc.gridy = 5;
        JScrollPane sp = new JScrollPane(contentArea);
        sp.setPreferredSize(new Dimension(420, 240));
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        form.add(sp, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        RoundedButton saveBtn = new RoundedButton("Save", UiStyles.SUCCESS, Color.WHITE);
        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);
        actions.add(saveBtn);
        actions.add(backBtn);

        gc.gridx = 0; gc.gridy = 6;
        form.add(actions, gc);

        add(form, BorderLayout.CENTER);

        RoundedButton dummy = new RoundedButton("", UiStyles.PANEL2, UiStyles.TEXT);

        backBtn.addActionListener(e -> {
            host.setContentPane(parent);
            host.revalidate();
            host.repaint();
        });

        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and content are required.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String catName = (String) categoryCombo.getSelectedItem();
            Long catId = null;
            if (catName != null && !catName.equals("Uncategorized")) {
                try {
                    catId = new CategoryDAO().findCategoryIdByName(userId, catName);
                } catch (SQLException ex2) {
                    JOptionPane.showMessageDialog(this, "Category lookup failed: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                new NoteDAO().createNote(userId, catId, title, content);
                parent.refresh();
                JOptionPane.showMessageDialog(this, "Note added successfully.");
                host.setContentPane(parent);
                host.revalidate();
                host.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save note: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        try {
            categoryCombo.removeAllItems();
            categoryCombo.addItem("Uncategorized");
            List<Category> categories = new CategoryDAO().listCategories(userId);
            for (Category c : categories) categoryCombo.addItem(c.getName());
        } catch (Exception ignored) {
            categoryCombo.removeAllItems();
            categoryCombo.addItem("Uncategorized");
        }
    }
}

