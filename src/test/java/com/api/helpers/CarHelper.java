package com.api.helpers;

import com.api.entity.Car;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * Creates a list of Car objects with the specified size.
     *
     * @param size the number of Car objects to create
     * @return a list of Car objects
     */
    public static List<Car> createCarList(int size) {
        List<Car> cars = new ArrayList<>();
        Set<String> licensePlates = new HashSet<>();
        for (int i = 0; i < size; i++) {
            Car car = new Car();
            car.setYear(2020 + i);

            String licensePlate;
            do {
                licensePlate = "ABC" + (1234 + i + licensePlates.size());
            } while (licensePlates.contains(licensePlate));

            licensePlates.add(licensePlate);
            car.setLicensePlate(licensePlate);
            car.setModel("Model" + i);
            car.setColor("Color" + i);
            cars.add(car);
        }
        return cars;
    }

}
