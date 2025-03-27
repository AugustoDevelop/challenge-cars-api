package com.api.repository;

import com.api.entity.User;
import com.api.util.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their login.
     *
     * @param login the login of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByLogin(String login);

    /**
     * Finds users by their status.
     *
     * @param status the status of the users
     * @return a list of users with the specified status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Finds a user by their ID and status.
     *
     * @param id     the ID of the user
     * @param status the status of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByIdAndStatus(Long id, UserStatus status);
}