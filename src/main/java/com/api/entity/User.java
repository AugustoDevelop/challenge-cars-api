package com.api.entity;

import com.api.util.UserStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entity representing a user in the system, implementing Spring Security's UserDetails interface.
 *
 * <p>This entity encapsulates user details, roles, and relationships with other entities like cars.
 * It also includes lifecycle callbacks for managing creation and update timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
@Entity(name = "USERS")
public class User implements UserDetails {
    /**
     * Unique identifier for the user, auto-generated upon creation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's first name.
     */
    @Column(name = "FIRST_NAME")
    private String firstName;

    /**
     * The user's last name.
     */
    @Column(name = "LAST_NAME")
    private String lastName;

    /**
     * The user's date of birth.
     */
    @Column(name = "BIRTHDAY")
    private String birthday;

    /**
     * Unique login identifier for the user.
     */
    @Column(name = "LOGIN", unique = true)
    private String login;

    /**
     * The user's password.
     */
    @Column(name = "PASSWORD")
    private String password;

    /**
     * The user's email address (must be unique).
     */
    @Column(name = "EMAIL", unique = true)
    private String email;

    /**
     * The user's phone number.
     */
    @Column(name = "PHONE")
    private String phone;

    /**
     * Timestamp when the user account was created (not updatable).
     */
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    /**
     * Timestamp of the user's last login.
     */
    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    /**
     * Status of the user account (e.g., active, inactive).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private UserStatus status;

    /**
     * URL of the user's profile photo.
     */
    @Column(name = "PHOTO_PROFILE_URL")
    private String photoProfileUrl;

    /**
     * List of cars owned by the user.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();


    /**
     * Initializes creation and update timestamps before persisting the entity.
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

    /**
     * Returns the authorities granted to the user based on their role.
     *
     * @return Collection of GrantedAuthority objects representing user roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "read");
    }

    /**
     * Returns the username (login) of the user.
     *
     * @return The username.
     */
    @Override
    public String getUsername() {
        return login;
    }

    /**
     * Checks if the user account is not expired.
     *
     * @return True if not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Default implementation always returns true
    }

    /**
     * Checks if the user account is not locked.
     *
     * @return True if not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Default implementation always returns true
    }

    /**
     * Checks if the user credentials are not expired.
     *
     * @return True if not expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Default implementation always returns true
    }

    /**
     * Checks if the user account is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true; // Default implementation always returns true
    }
}
