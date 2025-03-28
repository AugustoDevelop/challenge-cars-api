package com.api.exception;

import com.api.util.ErrorMessages;

/**
 * Exception thrown when invalid fields are encountered during data processing.
 *
 * <p>This exception is used to handle scenarios where data validation fails due to incorrect or malformed fields.
 * It provides a standardized way to report such errors and ensure consistent error handling.
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
