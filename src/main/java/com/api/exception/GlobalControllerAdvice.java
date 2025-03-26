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

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorMessages error = ex.getErrorMessages();
        return buildResponse(error.getMessage(), error.getHttpStatus().value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid fields");
        body.put("errorCode", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MissingFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMissingFieldsExceptions(MissingFieldsException ex) {
        return buildResponse(ex.getErrorMessages().getMessage(), ex.getErrorMessages().getHttpStatus().value());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message, int errorCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("errorCode", errorCode);
        return ResponseEntity.status(errorCode).body(body);
    }
}
