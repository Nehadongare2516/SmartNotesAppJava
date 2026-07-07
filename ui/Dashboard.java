package ui;

import dao.CategoryDAO;
import dao.NoteDAO;
import ui.components.RoundedButton;
import ui.components.UiStyles;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Dashboard extends JPanel {
    private final JFrame host;
    private final long userId;


    private final DefaultListModel<String> notesModel = new DefaultListModel<>();
    private final JList<String> notesList = new JList<>(notesModel);

    private boolean archivedView = false;

    public Dashboard(JFrame host, long userId) {
        this.host = host;
        this.userId = userId;

        setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel appName = new JLabel("Smart Notes");
        appName.setForeground(UiStyles.TEXT);
        appName.setFont(UiStyles.font(18, Font.BOLD));
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(20));

        RoundedButton dashboardBtn = new RoundedButton("Notes", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton addBtn = new RoundedButton("Add", UiStyles.ACCENT, Color.WHITE);
        RoundedButton searchBtn = new RoundedButton("Search", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton categoriesBtn = new RoundedButton("Categories", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton archiveBtn = new RoundedButton("Archive", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton profileBtn = new RoundedButton("Profile", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton settingsBtn = new RoundedButton("Settings", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton logoutBtn = new RoundedButton("Logout", new Color(255, 120, 120), Color.WHITE);


        dashboardBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        archiveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(addBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(searchBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(categoriesBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(archiveBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(profileBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(settingsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logoutBtn);


        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        main.setLayout(new BorderLayout());

        JLabel header = new JLabel("Dashboard");
        header.setForeground(UiStyles.TEXT);
        header.setFont(UiStyles.font(24, Font.BOLD));

        main.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout());

        notesList.setBackground(UiStyles.PANEL2);
        notesList.setForeground(UiStyles.TEXT);
        JScrollPane sp = new JScrollPane(notesList);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        center.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        RoundedButton viewBtn = new RoundedButton("View", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton editBtn = new RoundedButton("Edit", UiStyles.PANEL2, UiStyles.TEXT);
        RoundedButton deleteBtn = new RoundedButton("Delete", new Color(255, 80, 80), Color.WHITE);

        bottom.add(viewBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);

        center.add(bottom, BorderLayout.SOUTH);

        main.add(center, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        // Load notes
        refresh();

        dashboardBtn.addActionListener(e -> {
            archivedView = false;
            refresh();
        });

        addBtn.addActionListener(e -> host.setContentPane(new AddNote(host, userId, this)));

        searchBtn.addActionListener(e -> host.setContentPane(new SearchNotes(host, userId, this)));

        categoriesBtn.addActionListener(e -> host.setContentPane(new CategoriesManager(host, userId, this)));

        archiveBtn.addActionListener(e -> {
            archivedView = true;
            refresh();
        });


        logoutBtn.addActionListener(e -> {
            host.setContentPane(new Login(host));
            host.revalidate();
            host.repaint();
        });

        viewBtn.addActionListener(e -> {
            String selected = notesList.getSelectedValue();
            if (selected == null) return;
            long noteId = extractId(selected);
            host.setContentPane(new NotesList(host, userId, this, noteId, archivedView));
            host.revalidate();
            host.repaint();
        });

        editBtn.addActionListener(e -> {
            String selected = notesList.getSelectedValue();
            if (selected == null) return;
            long noteId = extractId(selected);
            host.setContentPane(new EditNote(host, userId, this, noteId));
            host.revalidate();
            host.repaint();
        });

        deleteBtn.addActionListener(e -> {
            String selected = notesList.getSelectedValue();
            if (selected == null) return;
            long noteId = extractId(selected);

            int res = JOptionPane.showConfirmDialog(this, "Delete this note?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_OPTION) return;

            try {
                NoteDAO dao = new NoteDAO();
                dao.deleteNote(userId, noteId);
                refresh();
                JOptionPane.showMessageDialog(this, "Note deleted.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void refresh() {
        notesModel.clear();
        try {
            List<model.Note> notes = new NoteDAO().listNotes(userId, archivedView);
            for (model.Note n : notes) {
                notesModel.addElement(format(n));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load notes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String format(model.Note n) {
        String flags = (n.isPinned() ? "[Pinned]" : "") + (n.isFavorite() ? "[Fav]" : "");
        if (!flags.isEmpty()) flags = flags + " ";
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

