package ui;

import dao.NoteDAO;
import ui.components.RoundedButton;
import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dedicated archive screen (in addition to Dashboard archive toggle).
 */
public class Archive extends JPanel {
    private final JFrame host;
    private final long userId;
    private final Dashboard parent;

    private final DefaultListModel<String> archivedModel = new DefaultListModel<>();
    private final JList<String> archivedList = new JList<>(archivedModel);
    private final boolean allowRestore = true;

    public Archive(JFrame host, long userId, Dashboard parent) {
        this.host = host;
        this.userId = userId;
        this.parent = parent;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Archive");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        main.setLayout(new BorderLayout());

        archivedList.setBackground(UiStyles.PANEL2);
        archivedList.setForeground(UiStyles.TEXT);

        JScrollPane sp = new JScrollPane(archivedList);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        main.add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);

        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton restoreBtn = new RoundedButton("Restore", UiStyles.ACCENT, Color.WHITE);
        RoundedButton deleteBtn = new RoundedButton("Delete", new Color(255, 80, 80), Color.WHITE);

        actions.add(backBtn);
        actions.add(restoreBtn);
        actions.add(deleteBtn);
        main.add(actions, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            host.setContentPane(parent);
            host.revalidate();
            host.repaint();
        });

        restoreBtn.addActionListener(e -> {
            String selected = archivedList.getSelectedValue();
            if (selected == null) return;
            long noteId = extractId(selected);
            if (noteId < 0) return;

            try {
                new NoteDAO().archive(userId, noteId, false);
                refresh();
                parent.refresh();
                JOptionPane.showMessageDialog(this, "Note restored.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Restore failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            String selected = archivedList.getSelectedValue();
            if (selected == null) return;
            long noteId = extractId(selected);
            if (noteId < 0) return;

            if (!util.DialogUtil.confirm(this, "Delete this archived note permanently?", "Confirm")) return;

            try {
                new NoteDAO().deleteNote(userId, noteId);
                refresh();
                parent.refresh();
                JOptionPane.showMessageDialog(this, "Note deleted.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refresh();
    }

    public void refresh() {
        archivedModel.clear();
        try {
            List<model.Note> notes = new NoteDAO().listNotes(userId, true);
            for (model.Note n : notes) archivedModel.addElement(format(n));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load archived notes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String format(model.Note n) {
        String flags = "";
        if (n.isPinned()) flags += "[Pinned] ";
        if (n.isFavorite()) flags += "[Fav] ";
        if (!flags.isEmpty()) flags = flags.trim() + " ";
        return n.getId() + " :: " + flags + n.getTitle();
    }

    private long extractId(String line) {
        try {
            return Long.parseLong(line.split("::")[0].trim());
        } catch (Exception e) {
            return -1;
        }
    }
}

