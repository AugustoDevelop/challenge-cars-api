package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when a requested resource is not found.
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