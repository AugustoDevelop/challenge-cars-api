package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when required fields are missing during data processing.
 *
 * <p>This exception is used to handle scenarios where essential data fields are not provided or are empty.
 * It provides a standardized way to report such errors and ensure consistent error handling.
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
