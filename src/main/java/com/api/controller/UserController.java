package com.api.controller;

import com.api.config.SecurityConfigurations;
import com.api.config.TokenService;
import com.api.dto.AuthenticationDTO;
import com.api.dto.LoginResponseDTO;
import com.api.dto.UserDto;
import com.api.dto.UserResponseDto;
import com.api.entity.User;
import com.api.exception.InvalidFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.UserServiceInterface;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import com.api.util.openapi.UserControllerOpenApi;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for managing user resources and authentication.
 *
 * <p>Handles user CRUD operations, photo uploads, and JWT-based authentication.
 *
 * <p>Endpoints are prefixed with {@code /api}
 */
@RestController
@RequestMapping(value = "/api")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
@RequiredArgsConstructor
public class UserController implements UserControllerOpenApi {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Service layer for user-related business logic.
     */
    @Autowired
    private UserServiceInterface userService;

    /**
     * Creates a new user.
     *
     * @param userDto the user data transfer object
     * @return HTTP 201 Created with persisted user details
     */
    @PostMapping("/users/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserDto userDto) {
        UserResponseDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Retrieves all users.
     *
     * @return HTTP 200 OK with list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves specific user by its unique identifier.
     *
     * @param id user ID path variable
     * @return HTTP 200 OK with requested user details
     * @throws ResourceNotFoundException if no user exists with given ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id user ID to delete
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates existing user details with validated data.
     *
     * @param id user ID to update
     * @param userDto validated updated user data
     * @return HTTP 200 OK with updated user entity
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Handles user photo uploads for specific users.
     *
     * @param id target user ID for photo association
     * @param file uploaded image file (supported formats: JPEG, PNG)
     * @return HTTP 200 OK with upload confirmation message
     * @throws ResourceNotFoundException if the user is not found
     * @throws InvalidFieldsException    if the photo upload fails
     */
    @PostMapping("/users/{id}/upload-photo")
    public ResponseEntity<UserResponseDto> uploadUserPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        UserResponseDto response = userService.uploadUserPhoto(id, file);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param body authentication data transfer object containing login and password
     * @return ResponseEntity containing the LoginResponseDTO with the generated token
     * @throws ResourceNotFoundException if authentication fails due to invalid credentials
     */
    @PostMapping("/singin")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO body) {
        User user = this.repository.findByLogin(body.login()).orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_LOGIN_OR_PASSWORD));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }
        return ResponseEntity.badRequest().build();
    }

}