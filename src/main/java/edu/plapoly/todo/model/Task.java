package edu.plapoly.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    private LocalDate dueDate;

    /** Optional separate reminder date/time, distinct from the due date. */
    private LocalDateTime reminderAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    private LocalDateTime dateCompleted;

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks = new ArrayList<>();

    public Task() {}

    public Task(String title, String description, LocalDate dueDate, Priority priority, User user) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.user = user;
        this.status = TaskStatus.PENDING;
    }

    /** Derived helper: is this task's due date in the past and not completed. */
    @Transient
    public boolean isOverdue() {
        return status != TaskStatus.COMPLETED && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    @Transient
    public boolean isReminderDue() {
        return status != TaskStatus.COMPLETED && reminderAt != null && !reminderAt.isAfter(LocalDateTime.now());
    }

    @Transient
    public int getSubtaskCompletedCount() {
        return (int) subtasks.stream().filter(Subtask::isCompleted).count();
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getReminderAt() { return reminderAt; }
    public void setReminderAt(LocalDateTime reminderAt) { this.reminderAt = reminderAt; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public LocalDateTime getDateCompleted() { return dateCompleted; }
    public void setDateCompleted(LocalDateTime dateCompleted) { this.dateCompleted = dateCompleted; }

    public List<Subtask> getSubtasks() { return subtasks; }
    public void setSubtasks(List<Subtask> subtasks) { this.subtasks = subtasks; }
}
