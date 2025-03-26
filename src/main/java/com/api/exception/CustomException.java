package com.api.exception;

import com.api.util.ErrorMessages;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorMessages errorMessages;

    public CustomException(ErrorMessages errorMessages) {
        super(errorMessages.getMessage());
        this.errorMessages = errorMessages;
    }
}