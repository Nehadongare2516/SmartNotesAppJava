package ui;

import dao.CategoryDAO;
import dao.NoteDAO;
import ui.components.RoundedButton;
import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;

public class NotesList extends JPanel {
    private final JFrame host;
    private final long userId;
    private final Dashboard parent;
    private final long noteId;
    private final boolean archivedView;

    private final JTextArea contentArea = new JTextArea();

    public NotesList(JFrame host, long userId, Dashboard parent, long noteId, boolean archivedView) {
        this.host = host;
        this.userId = userId;
        this.parent = parent;
        this.noteId = noteId;
        this.archivedView = archivedView;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Note Details");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        main.setLayout(new BorderLayout());

        contentArea.setEditable(false);
        contentArea.setBackground(UiStyles.PANEL2);
        contentArea.setForeground(UiStyles.TEXT);
        contentArea.setCaretColor(UiStyles.TEXT);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        contentArea.setFont(UiStyles.font(14, Font.PLAIN));

        JScrollPane sp = new JScrollPane(contentArea);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sp.setPreferredSize(new Dimension(520, 320));
        main.add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("Back", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton pinBtn = new RoundedButton("Pin/Unpin", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton favBtn = new RoundedButton("Favorite/Unfavorite", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton archiveBtn = new RoundedButton(archivedView ? "Restore" : "Archive", UiStyles.ACCENT, Color.WHITE);
        actions.add(backBtn);
        actions.add(pinBtn);
        actions.add(favBtn);
        actions.add(archiveBtn);

        main.add(actions, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);

        loadNote();

        backBtn.addActionListener(e -> {
            host.setContentPane(parent);
            host.revalidate();
            host.repaint();
        });

        pinBtn.addActionListener(e -> {
            try {
                var note = new NoteDAO().findById(userId, noteId);
                if (note == null) return;
                new NoteDAO().setPinned(userId, noteId, !note.isPinned());
                parent.refresh();
                loadNote();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Pin failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        favBtn.addActionListener(e -> {
            try {
                var note = new NoteDAO().findById(userId, noteId);
                if (note == null) return;
                new NoteDAO().setFavorite(userId, noteId, !note.isFavorite());
                parent.refresh();
                loadNote();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Favorite failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        archiveBtn.addActionListener(e -> {
            try {
                new NoteDAO().archive(userId, noteId, !archivedView);
                parent.refresh();
                host.setContentPane(parent);
                host.revalidate();
                host.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Archive failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadNote() {
        try {
            var note = new NoteDAO().findById(userId, noteId);
            if (note == null) {
                contentArea.setText("Note not found.");
                return;
            }
            String catName = "Uncategorized";
            if (note.getCategoryId() != null) {
                // Best-effort lookup by scanning categories.
                for (var c : new CategoryDAO().listCategories(userId)) {
                    if (c.getId() == note.getCategoryId()) {
                        catName = c.getName();
                        break;
                    }
                }
            }

            String flags = "";
            if (note.isPinned()) flags += "[Pinned] ";
            if (note.isFavorite()) flags += "[Favorite] ";
            flags += (note.isArchived() ? "[Archived]" : "[Active]");

            contentArea.setText(
                    "Title: " + note.getTitle() + "\n" +
                    "Category: " + catName + "\n" +
                    "Status: " + flags + "\n\n" +
                    note.getContent()
            );
        } catch (Exception ex) {
            contentArea.setText("Failed to load note: " + ex.getMessage());
        }
    }
}

