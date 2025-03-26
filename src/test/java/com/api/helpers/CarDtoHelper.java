package com.api.helpers;

import com.api.dto.CarDto;

/**
 * Helper class for creating CarDto objects.
 */
public class CarDtoHelper {

    /**
     * Creates a valid CarDto object.
     *
     * @return a CarDto object with valid data
     */
    public static CarDto createCarDto() {
        CarDto carDto = new CarDto();
        carDto.setYear(2020);
        carDto.setLicensePlate("ABC1234");
        carDto.setModel("Model");
        carDto.setColor("Cor");
        return carDto;
    }

    /**
     * Creates an invalid CarDto object.
     *
     * @return a CarDto object with invalid data
     */
    public static CarDto createCarDtoInvalid() {
        CarDto carDto = new CarDto();
        carDto.setYear(null);
        carDto.setLicensePlate("");
        carDto.setModel("");
        carDto.setColor("");
        return carDto;
    }
}
