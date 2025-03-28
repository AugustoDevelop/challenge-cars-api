package com.api.helpers;

import com.api.dto.CarDto;

import java.util.Random;

/**
 * Helper class for creating CarDto objects, facilitating the generation of both valid and invalid instances.
 *
 * <p>This class uses predefined arrays and random number generation to create fake data for valid CarDto objects,
 * making it useful for testing and other scenarios where diverse data is needed.
 */
public class CarDtoHelper {

    private static final String[] MODELS = {"Model1", "Model2", "Model3"};
    private static final String[] COLORS = {"Red", "Blue", "Green"};
    private static final String[] PLATE_PREFIXES = {"ABC", "DEF", "GHI"};
    private static final Random random = new Random();

    /**
     * Creates a valid CarDto object with fake data.
     *
     * @return a CarDto object with valid fake data
     */
    public static CarDto createCarDto() {
        int year = random.nextInt(10) + 2010; // Generates a random year between 2010 and 2019
        String plate = PLATE_PREFIXES[random.nextInt(PLATE_PREFIXES.length)] + String.format("%04d", random.nextInt(10000));
        String model = MODELS[random.nextInt(MODELS.length)];
        String color = COLORS[random.nextInt(COLORS.length)];

        return new CarDto(year, plate, model, color);
    }

    /**
     * Creates an invalid CarDto object with empty and null fields.
     *
     * @return a CarDto object with invalid data
     */
    public static CarDto createCarDtoInvalid() {
        return new CarDto(null, "", "", "");
    }
}
