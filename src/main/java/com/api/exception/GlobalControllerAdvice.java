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
 * Global exception handler for the application, providing centralized error handling and response standardization.
 *
 * <p>This class intercepts and manages various types of exceptions, ensuring consistent error responses are returned to clients.
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Handles custom exceptions by returning a standardized error response.
     *
     * @param ex the custom exception being handled
     * @return a ResponseEntity containing the error message and corresponding HTTP status code
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorMessages error = ex.getErrorMessages();
        return buildResponse(error.getMessage(), error.getHttpStatus().value());
    }

    /**
     * Handles validation exceptions by returning a bad request response with error details.
     *
     * @param ex the validation exception being handled
     * @return a ResponseEntity with a bad request status and error message
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
     * Handles missing fields exceptions by returning a bad request response with error details.
     *
     * @param ex the missing fields exception being handled
     * @return a ResponseEntity with a bad request status and error message
     */
    @ExceptionHandler(MissingFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMissingFieldsExceptions(MissingFieldsException ex) {
        return buildResponse(ex.getErrorMessages().getMessage(), ex.getErrorMessages().getHttpStatus().value());
    }

    /**
     * Builds a standardized error response entity with the given message and error code.
     *
     * @param message   the error message to be included in the response
     * @param errorCode the HTTP status code for the response
     * @return a ResponseEntity containing the error message and corresponding HTTP status code
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String message, int errorCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("errorCode", errorCode);
        return ResponseEntity.status(errorCode).body(body);
    }
}
