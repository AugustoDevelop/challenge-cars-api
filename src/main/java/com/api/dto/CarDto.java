package com.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing a car entity.
 *
 * <p>This DTO is used to encapsulate the essential details of a car,
 * including its year, license plate, model, and color.
 * It ensures that the required fields are validated before processing.
 *
 * @param year         The year of manufacture of the car (must not be null).
 * @param licensePlate The license plate number of the car (must not be blank).
 * @param model        The model name of the car (must not be blank).
 * @param color        The color of the car (must not be blank).
 */
public record CarDto(
        /**
         * The year of manufacture of the car.
         */
        @NotNull(message = "Year is required")
        Integer year,

        /**
         * The license plate number of the car.
         */
        @NotBlank(message = "License Plate is required")
        String licensePlate,

        /**
         * The model name of the car.
         */
        @NotBlank(message = "Model is required")
        String model,

        /**
         * The color of the car.
         */
        @NotBlank(message = "Color is required")
        String color
) {
}
