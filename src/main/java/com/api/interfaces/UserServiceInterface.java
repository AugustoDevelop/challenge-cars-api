package com.api.interfaces;

import com.api.dto.UserDto;
import com.api.dto.UserResponseDto;
import com.api.exception.DuplicateResourceException;
import com.api.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing user entities, providing methods for CRUD operations and photo uploads.
 *
 * <p>This interface defines the contract for user-related business logic, ensuring encapsulation and reusability of user management functionality.
 */
public interface UserServiceInterface {

    /**
     * Creates a new user based on the provided data transfer object.
     *
     * @param userDto the user data transfer object containing user details
     * @return the created User entity as a UserDto
     */
    UserResponseDto createUser(UserDto userDto);

    /**
     * Retrieves all available users in the system.
     *
     * @return a list of all User entities
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Retrieves a specific user by their unique identifier.
     *
     * @param id the ID of the user to retrieve
     * @return the User entity with the specified ID
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    UserResponseDto getUserById(Long id);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the ID of the user to delete
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    void deleteUserById(Long id);

    /**
     * Updates an existing user with the provided updated details.
     *
     * @param id          the ID of the user to update
     * @param userUpdates the user data transfer object containing updated user details
     * @return the updated User entity
     * @throws ResourceNotFoundException  if the user is not found
     * @throws DuplicateResourceException if the email or login already exists
     */
    UserResponseDto updateUser(Long id, UserDto userUpdates);

    /**
     * Uploads a photo for a specific user.
     *
     * @param userId the user ID
     * @param file   the photo file
     * @return the updated user with the photo URL
     */
    UserResponseDto uploadUserPhoto(Long userId, MultipartFile file);
}
