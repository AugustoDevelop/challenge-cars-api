package com.api.entity;

import com.api.util.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a user in the system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "BIRTHDAY")
    private String birthday;

    @Column(name = "LOGIN", unique = true)
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private UserStatus status;

    @Column(name = "PHOTO_URL")
    private String photoUrl;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    /**
     * Sets the creation and update timestamps before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    /**
     * Updates the update timestamp before updating the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}