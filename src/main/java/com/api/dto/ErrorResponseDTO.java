package com.api.dto;

/**
 * Data Transfer Object (DTO) for encapsulating error responses.
 *
 * <p>This DTO is used to standardize the structure of error messages returned
 * by the API, providing both a descriptive message and an associated error code.
 *
 * @param message   A descriptive error message indicating the nature of the error.
 * @param errorCode An integer representing the specific error code.
 */
public record ErrorResponseDTO(
        /**
         * A descriptive error message indicating the nature of the error.
         */
        String message,

        /**
         * An integer representing the specific error code.
         */
        Integer errorCode
) {
}
