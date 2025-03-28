package com.api.dto;

/**
 * Data Transfer Object (DTO) representing user authentication credentials.
 *
 * <p>Used to encapsulate the login and password provided during authentication attempts.
 *
 * @param login    The user's login identifier (e.g., username or email).
 * @param password The user's password.
 */
public record AuthenticationDTO(
        /**
         * The login of the user.
         */
        String login,

        /**
         * The password of the user.
         */
        String password
) {
}