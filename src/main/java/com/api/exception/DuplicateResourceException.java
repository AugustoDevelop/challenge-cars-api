package com.api.exception;

import com.api.util.ErrorMessages;

public class DuplicateResourceException extends CustomException {
    public DuplicateResourceException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}