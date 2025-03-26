package com.api.exception;

import com.api.util.ErrorMessages;

public class InvalidFieldsException extends CustomException {
    public InvalidFieldsException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}

