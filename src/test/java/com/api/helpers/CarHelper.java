package com.api.helpers;

import com.api.entity.Car;

/**
 * Helper class for creating Car objects.
 */
public class CarHelper {

    /**
     * Creates a valid Car object.
     *
     * @return a Car object with valid data
     */
    public static Car createCar() {
        Car car = new Car();
        car.setYear(2020);
        car.setLicensePlate("ABC1234");
        car.setModel("Model");
        car.setColor("Cor");
        return car;
    }

}
