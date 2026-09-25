-- ============================================================
-- Plateau State Polytechnic To-Do List Task Management System
-- PostgreSQL schema (Render production)
--
-- Like the MySQL script, this isn't usually run by hand — Hibernate
-- creates these tables automatically on first deploy. Kept here for
-- reference and manual setup if you prefer.
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) UNIQUE,
    matric_number   VARCHAR(50),
    dark_mode_preferred BOOLEAN NOT NULL DEFAULT FALSE,
    date_created    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    color_code   VARCHAR(10),
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TYPE priority_level AS ENUM ('LOW','MEDIUM','HIGH');
CREATE TYPE task_status AS ENUM ('PENDING','COMPLETED','OVERDUE');

CREATE TABLE IF NOT EXISTS tasks (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     BIGINT REFERENCES category(id) ON DELETE SET NULL,
    title           VARCHAR(150) NOT NULL,
    description     TEXT,
    due_date        DATE,
    reminder_at     TIMESTAMP,
    priority        priority_level NOT NULL DEFAULT 'MEDIUM',
    status          task_status NOT NULL DEFAULT 'PENDING',
    date_created    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_completed  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subtasks (
    id          BIGSERIAL PRIMARY KEY,
    task_id     BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    title       VARCHAR(200) NOT NULL,
    completed   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tasks_user ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_reminder ON tasks(reminder_at);
CREATE INDEX IF NOT EXISTS idx_category_user ON category(user_id);
