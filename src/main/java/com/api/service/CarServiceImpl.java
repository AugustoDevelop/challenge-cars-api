package com.api.service;

import com.api.config.TokenService;
import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.exception.InvalidFieldsException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.CarServiceInterface;
import com.api.repository.CarRepository;
import com.api.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for managing Car entities, providing methods for CRUD operations and photo uploads.
 *
 * <p>This class encapsulates the business logic for car management, ensuring validation and consistency of car data.
 */
@Service
@AllArgsConstructor
public class CarServiceImpl implements CarServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final Map<Long, Integer> carUsageCount = new HashMap<>();
    private final TokenService tokenService;
    /**
     * Creates a new car based on the provided data transfer object.
     *
     * @param carDto the car data transfer object
     * @return the created Car entity
     * @throws MissingFieldsException     if any required fields are missing
     *
     */
    public Car createCar(CarDto carDto) {
        logger.info("Creating car with license plate: {}", carDto.licensePlate());
        if (carDto.licensePlate().isBlank() || carDto.model().isBlank() || carDto.color().isBlank()) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
        if (carRepository.findByLicensePlate(carDto.licensePlate()).isPresent()) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
        User loggedUser = tokenService.getLoggedUser();
        Car car = new Car();
        car.setYear(carDto.year());
        car.setLicensePlate(carDto.licensePlate());
        car.setModel(carDto.model());
        car.setColor(carDto.color());
        car.setUser(loggedUser);
        Car createdCar = carRepository.save(car);
        logger.info("Car created successfully with ID: {}", createdCar.getId());
        return createdCar;
    }

    /**
     * Retrieves all available cars in the system.
     *
     * @return a list of all Car entities
     */
    public List<Car> getAllCars() {
        logger.info("Retrieving all cars");
        User loggedUser = tokenService.getLoggedUser();
        List<Car> cars = carRepository.findByUser_Login(loggedUser.getLogin());
        logger.info("Retrieved {} cars", cars.size());
        return cars;
    }
    /**
     * Retrieves a car by its ID and increments its usage amount.
     *
     * @param id the ID of the car
     * @return the car with the specified ID
     * @throws ResourceNotFoundException if the car is not found
     */
    public Car getCarById(Long id) {
        logger.info("Retrieving car with ID: {}", id);
        User loggedUser = tokenService.getLoggedUser();
        Car car = carRepository.findByIdAndUser_Login(id, loggedUser.getLogin())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        car.setUsageAmount(car.getUsageAmount() + 1);
        carRepository.save(car);
        updateUsageCount(car);
        logger.info("Car retrieved successfully with ID: {}", id);
        return car;
    }

    /**
     * Updates an existing car with the provided updated details.
     *
     * @param id     the car ID
     * @param carDto the car data transfer object with updates
     * @return the updated Car entity
     * @throws ResourceNotFoundException if the car is not found
     * @throws MissingFieldsException    if any required fields are missing
     */
    public Car updateCar(Long id, CarDto carDto) {
        logger.info("Updating car with ID: {}", id);
        User loggedUser = tokenService.getLoggedUser();
        validateCars(carDto);
        Optional<Car> existingCar = carRepository.findByIdAndUser_Login(id, loggedUser.getLogin());
        if (existingCar.isPresent()) {
            existingCar.get().setYear(carDto.year());
            existingCar.get().setLicensePlate(carDto.licensePlate());
            existingCar.get().setModel(carDto.model());
            existingCar.get().setColor(carDto.color());
            existingCar.get().setUser(loggedUser);
            Car updatedCar = carRepository.save(existingCar.get());
            logger.info("Car updated successfully with ID: {}", id);
            return updatedCar;
        } else {
            throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
        }
    }

    /**
     * Deletes a car by its ID.
     *
     * @param id the car ID
     * @throws ResourceNotFoundException if the car is not found
     */
    public void deleteCar(Long id) {
        logger.info("Deleting car with ID: {}", id);
        User loggedUser = tokenService.getLoggedUser();
        Car car = carRepository.findByIdAndUser_Login(id, loggedUser.getLogin())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        carRepository.delete(car);
        logger.info("Car deleted successfully with ID: {}", id);
    }

    /**
     * Uploads a photo for a specific car.
     *
     * @param carId the car ID
     * @param file  the photo file
     * @throws ResourceNotFoundException if the car is not found
     * @throws InvalidFieldsException    if the photo upload fails
     */
    public void uploadCarPhoto(Long carId, MultipartFile file) {
        logger.info("Uploading photo for car with ID: {}", carId);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get("uploads", "cars", String.valueOf(carId), file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            car.setPhotoCarUrl(path.toString());
            carRepository.save(car);
            logger.info("Photo uploaded successfully for car with ID: {}", carId);
        } catch (IOException e) {
            logger.error("Failed to upload photo for car with ID: {}", carId, e);
            throw new InvalidFieldsException(ErrorMessages.INVALID_PHOTO);
        }
    }

    /**
     * Validates the car data transfer object.
     *
     * @param carDto the car data transfer object
     * @throws MissingFieldsException if any required fields are missing
     */
    private void validateCars(CarDto carDto) {
        if (carDto.licensePlate().isBlank() ||
            carDto.model().isBlank() ||
            carDto.color().isBlank() ||
            carDto.year() == null
        ) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
    }

    /**
     * Updates the usage count for a car.
     *
     * @param car the car entity
     */
    private void updateUsageCount(Car car) {
        // Update car usage count
        carUsageCount.put(car.getId(), carUsageCount.getOrDefault(car.getId(), 0) + 1);
    }
}
