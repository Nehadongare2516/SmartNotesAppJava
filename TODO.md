# SmartNotesApp - Progress

## Generated so far
- README.md
- DatabaseConnection.java (auto-creates DB + tables)
- SQL script: sql/001_create_smartnotes.sql
- Models: User, Category, Note, Session
- Util: PasswordUtil, ValidationUtil, DialogUtil, ThemeManager
- DAO: UserDAO, CategoryDAO, NoteDAO (+ placeholder ExtraDAO)
- UI screens: Main, Login, Register, Dashboard, AddNote, EditNote, NotesList, Profile, Archive, Settings
- UI components: UiStyles, RoundedButton, ShadowPanel

## Still missing / to finish per requirements
- Search notes UI (query + results) and wire into Dashboard
- Filter notes UI (category filter + pinned/favorite/archive toggles)
- Pin/Favorite/Archive/Restore navigation consistency across screens
- Dedicated categories management UI (add/manage categories)
- User profile: show real username/email (add DAO by id or direct JDBC)
- Dark mode toggle UI + actual theming updates (UiStyles currently static)
- Logout clears Session and returns to Login; currently not clearing Session
- Fix compilation verification + ensure README run steps are correct for Windows/VS Code

## Next step
- Wire missing navigation buttons into Dashboard and implement the missing feature panels.

