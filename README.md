# To-Do List Task Management System
### Plateau State Polytechnic, Barkin Ladi

A web-based task management system built for students — create, organize, prioritize,
and track tasks with due dates, reminders, categories, and checklists. Works on desktop
and mobile browsers alike, and every student gets their own private account.

This README assumes **no prior experience** setting up a Java/Spring project. Follow it
top to bottom and you'll have the app running on your own computer, then deployed live
on the internet.

---

## 1. What's in this project

- **Backend:** Java 17 + Spring Boot
- **Frontend:** Thymeleaf (server-rendered HTML) + custom CSS — no separate frontend build step
- **Database:** MySQL while developing on your own machine, PostgreSQL once deployed on Render
- **Security:** Spring Security with hashed (BCrypt) passwords
- **Build tool:** Maven

You do **not** need to install anything related to MySQL and Postgres at the same time —
locally you'll only touch MySQL; Render's Postgres is configured separately and you'll
likely never run it yourself.

---

## 2. Prerequisites — install these first

| Tool | What it's for | Where to get it |
|---|---|---|
| **Java Development Kit (JDK) 17** | Compiles and runs the app | [Adoptium Temurin 17](https://adoptium.net/temurin/releases/?version=17) — pick the Windows installer |
| **VS Code** | Where you'll write/run the code | [code.visualstudio.com](https://code.visualstudio.com/) |
| **Extension Pack for Java** (VS Code extension) | Lets VS Code understand and run Java/Maven projects | Search "Extension Pack for Java" in VS Code's Extensions panel (`Ctrl+Shift+X`) and install it |
| **Spring Boot Extension Pack** (VS Code extension) | Adds Spring Boot–specific run/debug support | Same Extensions panel |
| **MySQL Server** (you mentioned you already have this) | Local database | If not: [dev.mysql.com/downloads/installer](https://dev.mysql.com/downloads/installer/) |
| **MySQL Workbench** (optional but helpful) | Visual tool to look at your database | Comes bundled with the MySQL installer above |
| **Git** | Version control | [git-scm.com](https://git-scm.com/) |

**Check your installs worked**, open a terminal (Command Prompt or PowerShell) and run:
```
java -version
mvn -version
mysql --version
git --version
```
Each should print a version number, not an error.

> If `mvn -version` says "not recognized" — VS Code's Java Extension Pack actually bundles
> its own Maven, so you can still open and run the project inside VS Code even without a
> separate Maven install. The command line `mvn` is only needed if you want to build from
> a terminal directly.

---

## 3. Set up the local MySQL database

You just need an empty database — the app creates its own tables automatically the first
time it runs.

1. Open MySQL Workbench (or the `mysql` command line).
2. Connect to your local server (usually `localhost`, port `3306`, user `root`).
3. Run:
   ```sql
   CREATE DATABASE IF NOT EXISTS todo_db;
   ```
   That's it — you don't need to create any tables by hand. (If you're curious what the
   tables look like, see `db/schema-mysql.sql` in this project — it's there for reference,
   not something you need to run.)

---

## 4. Configure the database connection

Open this file: `src/main/resources/application-dev.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

Update `username` and `password` to match your own local MySQL login. If you set a root
password when installing MySQL, put it after `password=`. Leave everything else as-is.

---

## 5. Run the project from a terminal

You don't need VS Code's Run button at all — this works from any terminal (Command
Prompt, PowerShell, or VS Code's own integrated terminal).

### 5.1 Make sure Maven is installed and on your PATH

Open a terminal and run:
```
mvn -version
```

- **If it prints a version number** (e.g. "Apache Maven 3.9.x"), skip to 5.2.
- **If it says "mvn is not recognized"**, Maven isn't installed as a standalone tool
  (VS Code's Java extension bundles its own copy, but that copy isn't automatically
  available to a plain terminal). Install it:
  1. Download the **Binary zip archive** from
     [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
  2. Unzip it somewhere permanent, e.g. `C:\Program Files\Apache\maven`
  3. Add its `bin` folder to your **System PATH**: search "Environment Variables"
     in the Windows Start menu → *Edit the system environment variables* → *Environment
     Variables* → under **System variables**, select `Path` → **Edit** → **New** →
     paste `C:\Program Files\Apache\maven\bin` → OK everywhere.
  4. **Close and reopen** your terminal (PATH changes don't apply to already-open
     terminals), then run `mvn -version` again to confirm.

### 5.2 Build and run

From the project folder (the one containing `pom.xml`):

```
mvn clean package -DskipTests
java -jar target\todo-app.jar
```

(On Mac/Linux, use `target/todo-app.jar` with a forward slash instead.)

The first `mvn clean package` compiles everything and produces a runnable file at
`target\todo-app.jar`. This step downloads dependencies the first time, so it can take
a few minutes — subsequent runs are much faster. Once you see:
```
Tomcat started on port 8080
Started TodoAppApplication
```
open a browser and go to **http://localhost:8080**.

**To stop the app:** press `Ctrl+C` in that terminal.

**Every time you make a code change**, repeat both commands above to rebuild and rerun.

> **Alternative, if you prefer VS Code's Run button:** open
> `src/main/java/edu/plapoly/todo/TodoAppApplication.java`, and click the ▷ **Run**
> icon that appears just above the `main` method. This does the same thing without a
> terminal, but requires the Java Extension Pack to have finished indexing the project
> first — if the button doesn't appear or doesn't work, use the terminal method above
> instead, which doesn't depend on VS Code at all.

---

## 6. Using the app

- **Register** an account (this is per-student — each person needs their own).
- **Dashboard:** see all your tasks, quick-filter by Today / This Week / Overdue,
  search, sort, and filter by category or status.
- **New task:** set a title, due date, priority, category, and an optional reminder
  date/time.
- **Task detail page:** add a checklist (subtasks) to break a task into smaller steps.
- **Categories:** create your own labels (e.g. "Assignments", "Personal") with a color.
- **Reports:** see your completion rate broken down by category.
- **Profile:** update your name/email, change your password, toggle dark mode.

---

## 7. Deploying to Render (making it live on the internet)

Render will run this app from a **Docker container** and connect it to a **managed
Postgres database** that Render hosts for you — you won't need to install Postgres
yourself.

### 7.1 Push this project to GitHub
1. Create a new (private or public) repository on GitHub.
2. From this project's folder:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPO.git
   git push -u origin main
   ```

### 7.2 Create the database on Render
1. Sign in at [render.com](https://render.com).
2. **New → PostgreSQL**.
3. Give it a name (e.g. `todo-db`), pick the free tier, create it.
4. Once it's ready, open it and note down (or keep the page open) the **Internal
   Database URL**, **Username**, and **Password** shown on its page.

### 7.3 Create the web service on Render
1. **New → Web Service**.
2. Connect your GitHub repository.
3. Render will detect the `Dockerfile` in this project automatically — leave the
   runtime as **Docker**.
4. Under **Environment Variables**, add:
   | Key | Value |
   |---|---|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `DATABASE_URL` | the Postgres connection string from step 7.2, but rewritten starting with `jdbc:postgresql://` instead of `postgres://` (see note below) |
   | `DATABASE_USERNAME` | the username from step 7.2 |
   | `DATABASE_PASSWORD` | the password from step 7.2 |

   > **About `DATABASE_URL`:** Render gives you a URL like
   > `postgres://user:pass@host:5432/dbname`. Spring/JDBC needs it in the form
   > `jdbc:postgresql://host:5432/dbname` instead (no username/password embedded in the
   > URL itself — those go in the separate `DATABASE_USERNAME` / `DATABASE_PASSWORD`
   > variables above).

5. Click **Create Web Service**. Render will build the Docker image and deploy it —
   this takes a few minutes the first time.
6. Once it's live, Render gives you a public URL like `https://todo-app-xxxx.onrender.com`
   — that's your live site. Share it with students.

### 7.4 Redeploying after changes
Any time you push new commits to the `main` branch on GitHub, Render automatically
rebuilds and redeploys — no extra steps needed.

---

## 8. Project structure

```
src/main/java/edu/plapoly/todo/
├── TodoAppApplication.java     # entry point
├── model/                      # User, Task, Category, Subtask entities
├── repository/                 # database query interfaces (Spring Data JPA)
├── service/                    # business logic, validation
├── controller/                 # handles web requests, decides which page to show
├── security/                   # login/authentication wiring
├── config/                     # Spring Security configuration
└── exception/                  # friendly error handling

src/main/resources/
├── application.properties           # shared settings
├── application-dev.properties       # local MySQL settings
├── application-prod.properties      # Render/Postgres settings
├── templates/                       # the actual web pages (Thymeleaf)
└── static/css, static/js            # styling and small client-side scripts

db/                              # reference SQL schema (not required to run by hand)
Dockerfile                       # used by Render to build and run the app
```

---

## 9. Troubleshooting

**"Access denied for user 'root'@'localhost'"**
Your MySQL password in `application-dev.properties` doesn't match your actual MySQL
root password. Fix it there.

**"Communications link failure" / "Unknown database 'todo_db'"**
MySQL isn't running, or you skipped step 3. Start the MySQL service, and make sure the
database exists.

**Port 8080 already in use**
Something else on your machine is using port 8080. Either close that program, or add
`server.port=8081` to `application-dev.properties` and use `http://localhost:8081` instead.

**VS Code doesn't show a Run button**
Make sure the Extension Pack for Java finished installing and the project loaded fully
(check the bottom status bar isn't still showing a spinner/loading icon).

**Changes to a page don't show up**
`application-dev.properties` already disables Thymeleaf caching for exactly this reason
— restart the app in VS Code (stop, then run again) and refresh your browser.

---

## 10. What's deliberately out of scope (for now)

To keep this version focused and reliable, the following were intentionally left out
and could be added later: email/SMS reminder delivery (only in-app reminders exist
today), sharing or delegating tasks between students, and an admin panel for staff.
