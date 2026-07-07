package ui;

import dao.CategoryDAO;
import model.Category;
import ui.components.RoundedButton;
import ui.components.UiStyles;
import util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Categories management screen: add and list categories for the current user.
 */
public class CategoriesManager extends JPanel {
    private final JFrame host;
    private final long userId;
    private final Dashboard parent;

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private final JTextField nameField = new JTextField();

    public CategoriesManager(JFrame host, long userId, Dashboard parent) {
        this.host = host;
        this.userId = userId;
        this.parent = parent;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Categories");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        main.setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbl = new JLabel("New category name");
        lbl.setForeground(UiStyles.MUTED);
        lbl.setFont(UiStyles.font(14, Font.PLAIN));
        gc.gridx = 0; gc.gridy = 0;
        top.add(lbl, gc);

        nameField.setBackground(UiStyles.PANEL2);
        nameField.setForeground(UiStyles.TEXT);
        nameField.setCaretColor(UiStyles.TEXT);
        nameField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        nameField.setFont(UiStyles.font(14, Font.PLAIN));
        gc.gridx = 0; gc.gridy = 1;
        nameField.setPreferredSize(new Dimension(420, 44));
        top.add(nameField, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        RoundedButton addBtn = new RoundedButton("Add Category", UiStyles.ACCENT, Color.WHITE);
        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);
        actions.add(addBtn);
        actions.add(backBtn);
        gc.gridx = 0; gc.gridy = 2;
        top.add(actions, gc);

        main.add(top, BorderLayout.NORTH);

        list.setBackground(UiStyles.PANEL2);
        list.setForeground(UiStyles.TEXT);
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        main.add(sp, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            host.setContentPane(parent);
            host.revalidate();
            host.repaint();
        });

        addBtn.addActionListener(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            if (name.isEmpty()) {
                DialogUtil.warn(this, "Enter a category name.", "Validation");
                return;
            }
            try {
                new CategoryDAO().createCategory(userId, name);
                nameField.setText("");
                refresh();
                DialogUtil.info(this, "Category added.", "Success");
            } catch (SQLException ex) {
                DialogUtil.error(this, "Failed to add category: " + ex.getMessage(), "Error");
            }
        });

        refresh();
    }

    private void refresh() {
        model.clear();
        try {
            List<Category> cats = new CategoryDAO().listCategories(userId);
            for (Category c : cats) {
                model.addElement(c.getName());
            }
        } catch (Exception ignored) {}
    }
}

