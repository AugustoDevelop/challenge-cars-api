package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when an attempt is made to create or access a duplicate resource.
 *
 * <p>This exception is used to handle scenarios where a resource already exists and cannot be duplicated,
 * providing a standardized error response for such situations.
 */
public class DuplicateResourceException extends CustomException {

    /**
     * Constructs a new DuplicateResourceException with the specified error messages.
     *
     * @param errorMessages the error messages associated with this exception
     */
    public DuplicateResourceException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}
