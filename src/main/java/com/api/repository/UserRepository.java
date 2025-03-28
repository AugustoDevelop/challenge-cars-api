package com.api.repository;

import com.api.entity.User;
import com.api.util.UserStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities, extending Spring Data JPA's JpaRepository for basic CRUD operations.
 *
 * <p>This interface provides additional custom query methods for retrieving users based on specific criteria such as email, login, status, and more.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their unique email address.
     *
     * @param email the email address of the user to find
     * @return an Optional containing the user if found, or empty if no user matches the given email
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a user by their unique login identifier.
     *
     * @param login the login of the user to find
     * @return an Optional containing the user if found, or empty if no user matches the given login
     */
    Optional<User> findByLogin(String login);


    /**
     * Retrieves a list of users with the specified status.
     *
     * @param status the status of the users to retrieve
     * @return a list of users matching the given status
     */
    @EntityGraph(attributePaths = "cars")
    List<User> findUsersByStatus(UserStatus status);

    /**
     * Retrieves a user by their ID and status.
     *
     * @param id     the ID of the user to find
     * @param status the status of the user
     * @return an Optional containing the user if found, or empty if no user matches the given ID and status
     */
    @EntityGraph(attributePaths = "cars")
    Optional<User> findByIdAndStatus(Long id, UserStatus status);
}
