package com.api.interfaces;

import com.api.dto.UserDto;
import com.api.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserServiceInterface {

    /**
     * Creates a new user.
     *
     * @param userDto the user data transfer object
     * @return the created user
     */
    User createUser(UserDto userDto);

    /**
     * Retrieves all users.
     *
     * @return the list of users
     */
    List<User> getAllUsers();

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user
     */
    User getUserById(Long id);

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     */
    void deleteUserById(Long id);

    /**
     * Updates a user.
     *
     * @param id          the user ID
     * @param userUpdates the user data transfer object with updates
     * @return the updated user
     */
    User updateUser(Long id, UserDto userUpdates);

    /**
     * Uploads a user's photo.
     *
     * @param userId the user ID
     * @param file   the photo file
     */
    void uploadUserPhoto(Long userId, MultipartFile file);
}