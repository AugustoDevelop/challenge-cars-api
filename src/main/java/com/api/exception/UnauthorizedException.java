package com.api.exception;

import com.api.util.ErrorMessages;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorMessages errorMessages) {
        super(errorMessages);
    }
}
