package com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    /**
     * The error message.
     */
    private String message;

    /**
     * The error code.
     */
    private Integer errorCode;
}