package edu.plapoly.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "subtasks")
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private boolean completed = false;

    public Subtask() {}

    public Subtask(String title, Task task) {
        this.title = title;
        this.task = task;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
