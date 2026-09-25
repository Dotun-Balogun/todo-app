-- ============================================================
-- Plateau State Polytechnic To-Do List Task Management System
-- MySQL schema (local development)
--
-- You normally DON'T need to run this by hand: with
-- spring.jpa.hibernate.ddl-auto=update (set in application-dev.properties),
-- Hibernate creates and updates these tables automatically the first time
-- you run the app. This script is provided so you can inspect the schema,
-- or create it manually if you'd rather not rely on auto-generation.
-- ============================================================

CREATE DATABASE IF NOT EXISTS todo_db;
USE todo_db;

CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) UNIQUE,
    matric_number   VARCHAR(50),
    dark_mode_preferred BOOLEAN NOT NULL DEFAULT FALSE,
    date_created    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    color_code   VARCHAR(10),
    user_id      BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    category_id     BIGINT,
    title           VARCHAR(150) NOT NULL,
    description     TEXT,
    due_date        DATE,
    reminder_at     DATETIME,
    priority        ENUM('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
    status          ENUM('PENDING','COMPLETED','OVERDUE') NOT NULL DEFAULT 'PENDING',
    date_created    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_completed  DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS subtasks (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id     BIGINT NOT NULL,
    title       VARCHAR(200) NOT NULL,
    completed   BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE INDEX idx_tasks_user ON tasks(user_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_reminder ON tasks(reminder_at);
CREATE INDEX idx_category_user ON category(user_id);
