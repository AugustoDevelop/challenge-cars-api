package com.api.controller;

import com.api.dto.UserDto;
import com.api.entity.User;
import com.api.interfaces.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing users.
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceInterface userService;

    /**
     * Creates a new user.
     *
     * @param userDto the user data transfer object
     * @return the created user
     */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDto userDto) {
        User createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Retrieves all users.
     *
     * @return the list of users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates a user.
     *
     * @param id      the user ID
     * @param userDto the user data transfer object
     * @return the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Uploads a user's photo.
     *
     * @param id   the user ID
     * @param file the photo file
     * @return a response entity with a success message
     */
    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<String> uploadUserPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        userService.uploadUserPhoto(id, file);
        return ResponseEntity.ok("User photo uploaded successfully");
    }
}