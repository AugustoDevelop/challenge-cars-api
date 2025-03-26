package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when a duplicate resource is encountered.
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