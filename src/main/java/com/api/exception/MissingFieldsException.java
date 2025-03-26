package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when required fields are missing.
 */
public class MissingFieldsException extends CustomException {

    /**
     * Constructs a new MissingFieldsException with the specified error messages.
     *
     * @param errorMessages the error messages associated with this exception
     */
    public MissingFieldsException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}