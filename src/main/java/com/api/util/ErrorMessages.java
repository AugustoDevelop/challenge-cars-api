package com.api.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessages {

    LOGIN_ALREADY_EXISTS("Login already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("Email already exists", HttpStatus.CONFLICT),
    INVALID_LOGIN_OR_PASSWORD("Invalid login or password", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("Unauthorized", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_INVALID_SESSION("Unauthorized - invalid session", HttpStatus.UNAUTHORIZED),
    LICENSE_PLATE_ALREADY_EXISTS("License plate already exists", HttpStatus.CONFLICT),
    INVALID_FIELDS("Invalid fields", HttpStatus.BAD_REQUEST),
    MISSING_FIELDS("Missing fields", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}

