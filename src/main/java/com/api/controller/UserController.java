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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Constructor for UserController.
     *
     * @param repository      the user repository
     * @param passwordEncoder the password encoder
     * @param tokenService    the token service
     * @param userService     the user service interface
     */
    @Autowired
    public UserController(UserRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService, UserServiceInterface userService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userService = userService;
    }

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
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserDto userDto) {
        logger.info("Received request to create user with login: {}", userDto.login());
        UserResponseDto createdUser = userService.createUser(userDto);
        logger.info("User created successfully with Login: {}", createdUser.login());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    /**
     * Retrieves all users.
     *
     * @return HTTP 200 OK with list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        logger.info("Received request to retrieve all users");
        List<UserResponseDto> users = userService.getAllUsers();
        logger.info("Retrieved {} users", users.size());
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
        logger.info("Received request to retrieve user with ID: {}", id);
        UserResponseDto user = userService.getUserById(id);
        logger.info("User retrieved successfully with ID: {}", id);
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
        logger.info("Received request to delete user with ID: {}", id);
        userService.deleteUserById(id);
        logger.info("User deleted successfully with ID: {}", id);
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
        logger.info("Received request to update user with ID: {}", id);
        UserResponseDto updatedUser = userService.updateUser(id, userDto);
        logger.info("User updated successfully with ID: {}", id);
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
        logger.info("Received request to upload photo for user with ID: {}", id);
        UserResponseDto response = userService.uploadUserPhoto(id, file);
        logger.info("Photo uploaded successfully for user with ID: {}", id);
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
        logger.info("Received login request for user with login: {}", body.login());
        User user = this.repository.findByLogin(body.login()).orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_LOGIN_OR_PASSWORD));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            logger.info("User authenticated successfully with login: {}", body.login());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }
        logger.warn("Failed login attempt for user with login: {}", body.login());
        return ResponseEntity.badRequest().build();
    }

}