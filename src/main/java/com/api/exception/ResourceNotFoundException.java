package com.api.exception;

import com.api.util.ErrorMessages;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}