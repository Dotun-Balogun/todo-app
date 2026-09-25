package edu.plapoly.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String fullName;

    @Email
    @Column(unique = true, length = 100)
    private String email;

    /** Matriculation / registration number, optional but useful for a polytechnic context. */
    @Column(length = 50)
    private String matricNumber;

    @Column(nullable = false)
    private boolean darkModePreferred = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public User() {}

    public User(String username, String passwordHash, String fullName, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatricNumber() { return matricNumber; }
    public void setMatricNumber(String matricNumber) { this.matricNumber = matricNumber; }

    public boolean isDarkModePreferred() { return darkModePreferred; }
    public void setDarkModePreferred(boolean darkModePreferred) { this.darkModePreferred = darkModePreferred; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
