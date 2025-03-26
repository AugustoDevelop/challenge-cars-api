package com.api.exception;

import com.api.util.ErrorMessages;

public class MissingFieldsException extends CustomException {
    public MissingFieldsException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}

