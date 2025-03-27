package com.api.exception;

import com.api.util.ErrorMessages;
import lombok.Getter;

/**
 * Custom exception class for handling application-specific errors.
 */
@Getter
public class CustomException extends RuntimeException {
    /**
     * The error messages associated with this exception.
     */
    private final ErrorMessages errorMessages;

    /**
     * Constructs a new CustomException with the specified error messages.
     *
     * @param errorMessages the error messages associated with this exception
     */
    public CustomException(ErrorMessages errorMessages) {
        super(errorMessages.getMessage());
        this.errorMessages = errorMessages;
    }
}