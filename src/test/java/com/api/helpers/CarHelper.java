package com.api.helpers;

import com.api.entity.Car;

public class CarHelper {

    public static Car createCar() {
        Car car = new Car();
        car.setYear(2020);
        car.setLicensePlate("ABC1234");
        car.setModel("Modelo");
        car.setColor("Cor");
        return car;
    }

}
