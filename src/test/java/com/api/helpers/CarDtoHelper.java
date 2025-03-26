package com.api.helpers;

import com.api.dto.CarDto;

public class CarDtoHelper {

    public static CarDto createCarDto() {
        CarDto carDto = new CarDto();
        carDto.setYear(2020);
        carDto.setLicensePlate("ABC1234");
        carDto.setModel("Modelo");
        carDto.setColor("Cor");
        return carDto;
    }

    public static CarDto createCarDtoInvalid() {
        CarDto carDto = new CarDto();
        carDto.setYear(null);
        carDto.setLicensePlate("");
        carDto.setModel("");
        carDto.setColor("");
        return carDto;
    }
}
