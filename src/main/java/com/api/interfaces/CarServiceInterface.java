package com.api.interfaces;

import com.api.dto.CarDto;
import com.api.entity.Car;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing Car entities.
 */
public interface CarServiceInterface {
    /**
     * Creates a new car.
     *
     * @param carDto the data transfer object containing car details
     * @return the created Car entity
     */
    Car createCar(CarDto carDto);

    /**
     * Retrieves all cars.
     *
     * @return a list of all Car entities
     */
    List<Car> getAllCars();

    /**
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car
     * @return the Car entity with the specified ID
     */
    Car getCarById(Long id);

    /**
     * Updates an existing car.
     *
     * @param id     the ID of the car to update
     * @param carDto the data transfer object containing updated car details
     * @return the updated Car entity
     */
    Car updateCar(Long id, CarDto carDto);

    /**
     * Deletes a car by its ID.
     *
     * @param id the ID of the car to delete
     */
    void deleteCar(Long id);

    /**
     * Uploads a photo for a car.
     *
     * @param carId the ID of the car
     * @param file  the photo file to upload
     */
    void uploadCarPhoto(Long carId, MultipartFile file);
}