package com.api.util.openapi;

import com.api.dto.AuthenticationDTO;
import com.api.dto.LoginResponseDTO;
import com.api.dto.UserDto;
import com.api.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OpenAPI interface for user-related operations.
 *
 * <p>This interface defines the endpoints for user management, including creating, retrieving, updating, and deleting users,
 * as well as uploading user photos and handling user authentication.
 */
@Tag(name = "User", description = "Operations related to users")
public interface UserControllerOpenApi {

    /**
     * Creates a new user.
     *
     * @param userDto the user data transfer object
     * @return HTTP 201 Created with persisted user details
     */
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/users/create")
    ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserDto userDto);

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    })
    @GetMapping("/users")
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    ResponseEntity<UserResponseDto> getUserById(@Parameter(description = "ID of the user to be retrieved") @PathVariable Long id);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the ID of the user to delete
     * @return HTTP 204 No Content if the user is deleted successfully
     */
    @Operation(summary = "Delete user by ID", description = "Deletes a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    ResponseEntity<Void> deleteUserById(@Parameter(description = "ID of the user to be deleted") @PathVariable Long id);

    /**
     * Updates an existing user with validated data.
     *
     * @param id      the ID of the user to update
     * @param userDto the updated user data transfer object
     * @return the updated user entity
     */
    @Operation(summary = "Update user details", description = "Updates the details of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{id}")
    ResponseEntity<UserResponseDto> updateUser(@Parameter(description = "ID of the user to be updated") @PathVariable Long id, @RequestBody @Valid UserDto userDto);

    /**
     * Uploads a photo for a specific user.
     *
     * @param id   the ID of the user
     * @param file the photo file to upload
     * @return the updated user entity with the uploaded photo
     */
    @Operation(summary = "Upload user photo", description = "Uploads a photo for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/users/{id}/upload-photo")
    ResponseEntity<UserResponseDto> uploadUserPhoto(@Parameter(description = "ID of the user to upload the photo for") @PathVariable Long id, @RequestParam("file") MultipartFile file);

    /**
     * Handles user login and authentication.
     *
     * @param data the authentication data transfer object
     * @return the login response containing the JWT token
     */
    @Operation(summary = "User login", description = "Authenticates a user and generates a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/singin")
    ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data);
}