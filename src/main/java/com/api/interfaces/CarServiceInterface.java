package com.api.interfaces;

import com.api.dto.CarDto;
import com.api.entity.Car;

import java.util.List;

public interface CarServiceInterface {
    Car createCar(CarDto carDto);
    List<Car> getAllCars();
    Car getCarById(Long id);
    Car updateCar(Long id, CarDto carDto);
    void deleteCar(Long id);
}