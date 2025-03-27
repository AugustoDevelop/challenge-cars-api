package com.api.service;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.exception.InvalidFieldsException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.CarServiceInterface;
import com.api.repository.CarRepository;
import com.api.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
 * Service implementation for managing cars.
 */
@Service
@AllArgsConstructor
public class CarServiceImpl implements CarServiceInterface {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final Map<Long, Integer> carUsageCount = new HashMap<>();

    /**
     * Creates a new car.
     *
     * @param carDto the car data transfer object
     * @return the created car
     * @throws MissingFieldsException if any required fields are missing
     */
    public Car createCar(CarDto carDto) {
        if (carDto.getLicensePlate().isBlank() || carDto.getModel().isBlank() || carDto.getColor().isBlank()) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
        if (carRepository.findByLicensePlate(carDto.getLicensePlate()).isPresent()) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
        Car car = modelMapper.map(carDto, Car.class);
        return carRepository.save(car);
    }

    /**
     * Retrieves all cars.
     *
     * @return a list of cars
     */
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    /**
     /**
     * Retrieves a car by its ID and increments its usage amount.
     *
     * @param id the ID of the car
     * @return the car with the specified ID
     * @throws ResourceNotFoundException if the car is not found
     */
    public Car getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        car.setUsageAmount(car.getUsageAmount() + 1);
        carRepository.save(car);

        // Update usage count
        updateUsageCount(car);

        return car;
    }

    /**
     * Updates a car.
     *
     * @param id     the car ID
     * @param carDto the car data transfer object with updates
     * @return the updated car
     * @throws ResourceNotFoundException if the car is not found
     */
    public Car updateCar(Long id, CarDto carDto) {
        validateCars(carDto);
        Optional<Car> existingCar = getCarByLicensePlate(carDto.getLicensePlate());
        if (existingCar.isPresent()) {
            existingCar.get().setYear(carDto.getYear());
            existingCar.get().setLicensePlate(carDto.getLicensePlate());
            existingCar.get().setModel(carDto.getModel());
            existingCar.get().setColor(carDto.getColor());
            return carRepository.save(existingCar.get());
        } else {
            throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
        }
    }

    /**
     * Deletes a car by ID.
     *
     * @param id the car ID
     * @throws ResourceNotFoundException if the car is not found
     */
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
        }
        carRepository.deleteById(id);
    }

    /**
     * Uploads a photo for the car.
     *
     * @param carId the car ID
     * @param file  the photo file
     * @throws ResourceNotFoundException if the car is not found
     * @throws InvalidFieldsException    if the photo upload fails
     */
    public void uploadCarPhoto(Long carId, MultipartFile file) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        try {
            byte[] bytes = file.getBytes();
            file.getOriginalFilename();
            Path path = Paths.get("uploads", "cars", String.valueOf(carId), file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            car.setPhotoUrl(path.toString());
            carRepository.save(car);
        } catch (IOException e) {
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
        if (carDto.getLicensePlate().isBlank() ||
            carDto.getModel().isBlank() ||
            carDto.getColor().isBlank() ||
            carDto.getYear() == null
        ) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
    }

    /**
     * Retrieves a car by license plate.
     *
     * @param licensePlate the car license plate
     * @return an optional car
     * @throws ResourceNotFoundException if the car is not found
     */
    private Optional<Car> getCarByLicensePlate(String licensePlate) {
        return Optional.ofNullable(carRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS)));
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

