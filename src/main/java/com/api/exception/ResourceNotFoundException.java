package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when a requested resource cannot be found.
 *
 * <p>This exception is used to handle scenarios where a requested resource, such as a user or a car, does not exist.
 * It provides a standardized way to report such errors and ensure consistent error handling.
 */
public class ResourceNotFoundException extends CustomException {

    /**
     * Constructs a new ResourceNotFoundException with the specified error messages.
     *
     * @param errorMessages the error messages associated with this exception
     */
    public ResourceNotFoundException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}
