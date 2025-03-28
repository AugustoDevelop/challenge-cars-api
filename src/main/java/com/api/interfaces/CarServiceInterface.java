package com.api.interfaces;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.exception.InvalidFieldsException;
import com.api.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing Car entities, providing methods for CRUD operations and photo uploads.
 *
 * <p>This interface defines the contract for car-related business logic, ensuring encapsulation and reusability of car management functionality.
 */
public interface CarServiceInterface {
    /**
     * Creates a new car based on the provided data transfer object.
     *
     * @param carDto the data transfer object containing car details
     * @return the created Car entity
     */
    Car createCar(CarDto carDto);

    /**
     * Retrieves all available cars in the system.
     *
     * @return a list of all Car entities
     */
    List<Car> getAllCars();

    /**
     * Retrieves a specific car by its unique identifier.
     *
     * @param id the ID of the car to retrieve
     * @return the Car entity with the specified ID
     * @throws ResourceNotFoundException if no car exists with the given ID
     */
    Car getCarById(Long id);

    /**
     * Updates an existing car with the provided updated details.
     *
     * @param id     the ID of the car to update
     * @param carDto the data transfer object containing updated car details
     * @return the updated Car entity
     * @throws ResourceNotFoundException if no car exists with the given ID
     * @throws ResourceNotFoundException if no car exists with the given ID
     */
    Car updateCar(Long id, CarDto carDto);

    /**
     * Deletes a car by its unique identifier.
     *
     * @param id the ID of the car to delete
     * @throws ResourceNotFoundException if no car exists with the given ID
     */
    void deleteCar(Long id);

    /**
     * Uploads a photo for a specific car.
     *
     * @param carId the ID of the car
     * @param file  the photo file to upload
     * @throws ResourceNotFoundException if the car is not found
     * @throws InvalidFieldsException    if the photo upload fails
     */
    void uploadCarPhoto(Long carId, MultipartFile file);
}
