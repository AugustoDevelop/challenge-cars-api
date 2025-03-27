package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when an unauthorized action is attempted.
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