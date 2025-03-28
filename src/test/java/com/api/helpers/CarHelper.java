package com.api.helpers;

import com.api.entity.Car;
import com.api.entity.User;

import java.util.*;

/**
 * Helper class for creating Car objects, facilitating the generation of both single instances and lists.
 *
 * <p>This class uses predefined arrays and random number generation to create fake data for valid Car objects,
 * making it useful for testing and other scenarios where diverse data is needed.
 */
public class CarHelper {

    private static final String[] MODELS = {"Model1", "Model2", "Model3", "Model4", "Model5"};
    private static final String[] COLORS = {"Red", "Blue", "Green", "Yellow", "Purple"};
    private static final String[] PLATE_PREFIXES = {"ABC", "DEF", "GHI", "JKL", "MNO"};
    private static final Random random = new Random();

    /**
     * Creates a valid Car object with fake data.
     *
     * @return a Car object with valid fake data
     */
    public static Car createCar() {
        Car car = new Car();
        car.setYear(random.nextInt(10) + 2010); // Generates a random year between 2010 and 2019
        car.setLicensePlate(generateUniqueLicensePlate());
        car.setModel(MODELS[random.nextInt(MODELS.length)]);
        car.setColor(COLORS[random.nextInt(COLORS.length)]);
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
                licensePlate = generateUniqueLicensePlate();
            } while (licensePlates.contains(licensePlate));

            licensePlates.add(licensePlate);
            car.setLicensePlate(licensePlate);
            car.setModel(MODELS[random.nextInt(MODELS.length)]);
            car.setColor(COLORS[random.nextInt(COLORS.length)]);
            cars.add(car);
        }
        return cars;
    }

    /**
     * Creates a list of Car entities with valid fake values and associates them with the given user.
     *
     * @param count the number of cars to create
     * @param user  the user to associate with the cars
     * @return a list of Car entities
     */
    public static List<Car> createCarList(int count, User user) {
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Car car = new Car();
            car.setYear(random.nextInt(10) + 2010); // Generates a random year between 2010 and 2019
            car.setLicensePlate(PLATE_PREFIXES[random.nextInt(PLATE_PREFIXES.length)] + String.format("%04d", random.nextInt(10000)));
            car.setModel(MODELS[random.nextInt(MODELS.length)]);
            car.setColor(COLORS[random.nextInt(COLORS.length)]);
            car.setUser(user); // Associate the car with the user
            cars.add(car);
        }
        return cars;
    }

    /**
     * Generates a unique license plate.
     *
     * @return a unique license plate
     */
    private static String generateUniqueLicensePlate() {
        String prefix = PLATE_PREFIXES[random.nextInt(PLATE_PREFIXES.length)];
        String number = String.format("%04d", random.nextInt(10000));
        return prefix + number;
    }
}
