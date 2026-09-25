package edu.plapoly.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    /** Hex color code used for visual tagging, e.g. #B3242F */
    @Column(length = 10)
    private String colorCode = "#8A8578";

    /** Each category belongs to one student, so categories don't leak across accounts. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Category() {}

    public Category(String name, String colorCode, User user) {
        this.name = name;
        this.colorCode = colorCode;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
