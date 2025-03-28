package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when an unauthorized action is attempted, indicating that the user lacks the necessary permissions or authentication.
 *
 * <p>This exception is used to handle scenarios where a user tries to access a protected resource without proper authorization.
 * It provides a standardized way to report such errors and ensure consistent error handling.
 */
public class UnauthorizedException extends CustomException {

    /**
     * Constructs a new UnauthorizedException with the specified error messages.
     *
     * @param errorMessages the error messages associated with this exception
     */
    public UnauthorizedException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}
