# Smart Notes App (Java Swing + JDBC + MySQL)

A professional desktop “Smart Notes” application built with:
- Java 17+
- Java Swing (modern UI)
- JDBC
- MySQL

## Features
- User Registration / Login
- Dashboard
- Add / View / Edit / Delete Notes
- Search Notes
- Filter Notes (category)
- Pin Notes
- Favorite Notes
- Archive / Restore Notes
- Categories
- User Profile
- Change Password
- Logout
- Dark Mode
- Input validation + exception handling

## Prerequisites
1. **MySQL Server** running
2. **MySQL Connector/J** added to the project

### MySQL setup (database + tables)
The app auto-creates the database and tables on first run.

Database name: `smartnotes`

Default credentials used by the app:
- Host: `localhost`
- Port: `3306`
- Database: `smartnotes`
- Username: `root`
- Password: `root`

If your MySQL password is different, change it in:
- `src/database/DatabaseConnection.java`

Look for:
- `private static final String DB_PASSWORD = "root";`

## Add MySQL JDBC Driver (Connector/J)
Download **MySQL Connector/J 9.3.0** (recommended):
- https://dev.mysql.com/downloads/connector/j/

Then:
1. Copy `mysql-connector-j-9.3.0.jar` (or compatible latest jar you have) into:
   - `lib/`
2. Ensure VS Code classpath picks it up (see “How to run”).

## How to run in VS Code
### 1) Install JDK 17+
Ensure `java` and `javac` are available in your PATH.

### 2) Add MySQL JDBC driver
Download MySQL Connector/J (9.3.0 recommended) and copy the jar into:
- `lib/`

### 3) Configure MySQL credentials (if needed)
Edit:
- `src/database/DatabaseConnection.java`
Look for:
- `private static final String DB_PASSWORD = "root";`

### 4) Compile + run (Windows)
From VS Code terminal in this folder (`SmartNotesApp`), run:

```bat
run.bat
```

This batch file compiles all Java sources into `build/` and runs:
- `main.Main`


## Important files
- `src/main/Main.java` : program entry
- `src/database/DatabaseConnection.java` : MySQL connection + schema init
- `src/dao/*` : JDBC Data Access Objects
- `src/ui/*` : Swing screens

## Credits
Built using MVC + JDBC best practices.

