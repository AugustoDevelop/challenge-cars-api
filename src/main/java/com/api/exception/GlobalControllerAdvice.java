package com.api.exception;

import com.api.util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Handles custom exceptions.
     *
     * @param ex the custom exception
     * @return a response entity containing the error message and status code
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorMessages error = ex.getErrorMessages();
        return buildResponse(error.getMessage(), error.getHttpStatus().value());
    }

    /**
     * Handles validation exceptions.
     *
     * @param ex the validation exception
     * @return a response entity containing the error message and status code
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid fields");
        body.put("errorCode", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles missing fields exceptions.
     *
     * @param ex the missing fields exception
     * @return a response entity containing the error message and status code
     */
    @ExceptionHandler(MissingFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMissingFieldsExceptions(MissingFieldsException ex) {
        return buildResponse(ex.getErrorMessages().getMessage(), ex.getErrorMessages().getHttpStatus().value());
    }

    /**
     * Builds a response entity with the given message and error code.
     *
     * @param message   the error message
     * @param errorCode the error code
     * @return a response entity containing the error message and status code
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String message, int errorCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("errorCode", errorCode);
        return ResponseEntity.status(errorCode).body(body);
    }
}