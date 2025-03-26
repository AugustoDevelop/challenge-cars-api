package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when invalid fields are encountered.
 */
public class InvalidFieldsException extends CustomException {

    /**
     * Constructs a new InvalidFieldsException with the specified error messages.
     *
     * @param errorMessages the error messages associated with this exception
     */
    public InvalidFieldsException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}