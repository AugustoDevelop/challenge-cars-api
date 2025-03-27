package com.api.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Enum representing various error messages and their associated HTTP status codes.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorMessages {

    /**
     * Error message indicating that the login already exists.
     */
    LOGIN_ALREADY_EXISTS("Login already exists", HttpStatus.CONFLICT),

    /**
     * Error message indicating that the email already exists.
     */
    EMAIL_ALREADY_EXISTS("Email already exists", HttpStatus.CONFLICT),

    /**
     * Error message indicating invalid login or password.
     */
    INVALID_LOGIN_OR_PASSWORD("Invalid login or password", HttpStatus.BAD_REQUEST),

    /**
     * Error message indicating unauthorized access.
     */
    UNAUTHORIZED("Unauthorized", HttpStatus.UNAUTHORIZED),

    /**
     * Error message indicating unauthorized access due to an invalid session.
     */
    UNAUTHORIZED_INVALID_SESSION("Unauthorized - invalid session", HttpStatus.UNAUTHORIZED),

    /**
     * Error message indicating that the license plate already exists.
     */
    LICENSE_PLATE_ALREADY_EXISTS("License plate already exists", HttpStatus.CONFLICT),

    /**
     * Error message indicating invalid fields.
     */
    INVALID_FIELDS("Invalid fields", HttpStatus.BAD_REQUEST),

    /**
     * Error message indicating missing fields.
     */
    MISSING_FIELDS("Missing fields", HttpStatus.BAD_REQUEST),

    /**
     * Error message indicating a failure to upload a car photo.
     */
    INVALID_PHOTO("Failed to upload car photo", HttpStatus.BAD_REQUEST);

    /**
     * The error message.
     */
    private final String message;

    /**
     * The HTTP status code associated with the error message.
     */
    private final HttpStatus httpStatus;
}