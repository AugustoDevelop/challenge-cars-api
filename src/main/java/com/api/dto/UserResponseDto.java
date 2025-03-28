package com.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Data Response Object (DTO) for encapsulating user information.
 *
 * <p>This DTO is used to represent essential user details, including personal data and associated vehicles.
 * It ensures that required fields are validated before processing.
 *
 * @param firstName The user's first name (must not be blank).
 * @param lastName  The user's last name (must not be blank).
 * @param email     The user's email address (must be valid and not blank).
 * @param birthday  The user's date of birth (must not be blank).
 * @param login     The user's unique login identifier (must not be blank).
 * @param phone     The user's phone number (must not be blank).
 * @param cars      A list of cars associated with the user.
 */
public record UserResponseDto(
        /**
         * The user's first name.
         */
        @NotBlank(message = "First name is required")
        String firstName,

        /**
         * The user's last name.
         */
        @NotBlank(message = "Last name is required")
        String lastName,

        /**
         * The user's email address.
         */
        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email,

        /**
         * The user's date of birth.
         */
        @NotBlank(message = "Birthday is required")
        String birthday,

        /**
         * The user's unique login identifier.
         */
        @NotBlank(message = "Login is required")
        String login,

        /**
         * The user's phone number.
         */
        @NotBlank(message = "Phone is required")
        String phone,

        /**
         * A list of cars owned by the user.
         */
        List<CarDto> cars
) {
}
