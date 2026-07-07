package ui;

import dao.CategoryDAO;
import dao.NoteDAO;
import model.Category;
import ui.components.RoundedButton;
import ui.components.UiStyles;
import util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SearchNotes extends JPanel {
    private final JFrame host;
    private final long userId;
    private final Dashboard parent;

    private final JTextField queryField = new JTextField();
    private final JComboBox<String> categoryCombo = new JComboBox<>();
    private final JCheckBox archivedCheck = new JCheckBox("Show archived");
    private final DefaultListModel<String> resultsModel = new DefaultListModel<>();
    private final JList<String> resultsList = new JList<>(resultsModel);

    public SearchNotes(JFrame host, long userId, Dashboard parent) {
        this.host = host;
        this.userId = userId;
        this.parent = parent;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Search Notes");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        main.setLayout(new BorderLayout());

        JPanel filters = new JPanel(new GridBagLayout());
        filters.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        queryField.setBackground(UiStyles.PANEL2);
        queryField.setForeground(UiStyles.TEXT);
        queryField.setCaretColor(UiStyles.TEXT);
        queryField.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        queryField.setFont(UiStyles.font(14, Font.PLAIN));

        categoryCombo.setBackground(UiStyles.PANEL2);
        categoryCombo.setForeground(UiStyles.TEXT);
        categoryCombo.setFont(UiStyles.font(14, Font.PLAIN));

        archivedCheck.setForeground(UiStyles.MUTED);
        archivedCheck.setOpaque(false);

        gc.gridx = 0; gc.gridy = 0;
        JLabel qLbl = new JLabel("Query");
        qLbl.setForeground(UiStyles.MUTED);
        qLbl.setFont(UiStyles.font(14, Font.PLAIN));
        filters.add(qLbl, gc);

        gc.gridx = 0; gc.gridy = 1;
        queryField.setPreferredSize(new Dimension(420, 44));
        filters.add(queryField, gc);

        gc.gridx = 1; gc.gridy = 0;
        JLabel cLbl = new JLabel("Category");
        cLbl.setForeground(UiStyles.MUTED);
        cLbl.setFont(UiStyles.font(14, Font.PLAIN));
        filters.add(cLbl, gc);

        gc.gridx = 1; gc.gridy = 1;
        categoryCombo.setPreferredSize(new Dimension(220, 44));
        filters.add(categoryCombo, gc);

        gc.gridx = 2; gc.gridy = 1;
        filters.add(archivedCheck, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        RoundedButton searchBtn = new RoundedButton("Search", UiStyles.ACCENT, Color.WHITE);
        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);
        actions.add(searchBtn);
        actions.add(backBtn);

        gc.gridx = 0; gc.gridy = 2;
        gc.gridwidth = 3;
        filters.add(actions, gc);

        main.add(filters, BorderLayout.NORTH);

        resultsList.setBackground(UiStyles.PANEL2);
        resultsList.setForeground(UiStyles.TEXT);
        JScrollPane sp = new JScrollPane(resultsList);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        main.add(sp, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            host.setContentPane(parent);
            host.revalidate();
            host.repaint();
        });

        searchBtn.addActionListener(e -> {
            String query = queryField.getText().trim();
            if (query.isEmpty()) {
                DialogUtil.warn(this, "Enter a search query.", "Validation");
                return;
            }

            String catName = (String) categoryCombo.getSelectedItem();
            Long catId = null;
            if (catName != null && !catName.equals("All Categories")) {
                try {
                    catId = new CategoryDAO().findCategoryIdByName(userId, catName);
                } catch (Exception ex) {
                    DialogUtil.error(this, "Category lookup failed: " + ex.getMessage(), "Error");
                    return;
                }
            }

            boolean archived = archivedCheck.isSelected();
            try {
                resultsModel.clear();
                var notes = new NoteDAO().searchNotes(userId, query, catId, archived);
                for (var n : notes) {
                    resultsModel.addElement(format(n));
                }
            } catch (Exception ex) {
                DialogUtil.error(this, "Search failed: " + ex.getMessage(), "Error");
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        categoryCombo.addItem("All Categories");
        try {
            List<Category> cats = new CategoryDAO().listCategories(userId);
            for (Category c : cats) categoryCombo.addItem(c.getName());
        } catch (Exception ignored) {
        }
    }

    private String format(model.Note n) {
        String flags = (n.isPinned() ? "[Pinned]" : "") + (n.isFavorite() ? "[Fav]" : "");
        if (!flags.isEmpty()) flags += " ";
        return n.getId() + " :: " + flags + n.getTitle();
    }
}

