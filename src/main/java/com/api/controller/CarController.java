package com.api.controller;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.exception.InvalidFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.CarServiceInterface;
import com.api.util.openapi.CarControllerOpenApi;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for managing car resources and operations.
 *
 * <p>Exposes endpoints for:
 * <ul>
 *   <li>Car CRUD operations</li>
 *   <li>Photo upload functionality</li>
 * </ul>
 *
 * <p>All endpoints are prefixed with {@code /api2/cars}
 */
@RestController
@RequestMapping(value = "/api/cars")
public class CarController implements CarControllerOpenApi {

    /**
     * Service layer for handling car-related business logic
     */
    private final CarServiceInterface carService;

    /**
     * Constructor for CarController.
     *
     * @param carService the car service interface
     */
    @Autowired
    public CarController(CarServiceInterface carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car with validated data.
     *
     * @param carDto validated car data transfer object
     * @return HTTP 201 Created with persisted car entity
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody @Valid CarDto carDto) {
        Car createdCar = carService.createCar(carDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
    }

    /**
     * Retrieves all cars.
     *
     * @return a list of all cars
     */
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    /**
     * Retrieves specific car by its unique identifier.
     *
     * @param id car ID path variable
     * @return HTTP 200 OK with requested car details
     * @throws ResourceNotFoundException if no car exists with given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    /**
     * Updates existing car details with validated data.
     *
     * @param id car ID to update
     * @param carDto validated updated car data
     * @return HTTP 200 OK with updated car entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody @Valid CarDto carDto) {
        Car updatedCar = carService.updateCar(id, carDto);
        return ResponseEntity.ok(updatedCar);
    }

    /**
     * Deletes a car by its unique identifier.
     *
     * @param id car ID to delete
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarById(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handles car photo uploads for specific vehicles.
     *
     * @param carId target car ID for photo association
     * @param file uploaded image file (supported formats: JPEG, PNG)
     * @return HTTP 200 OK with upload confirmation message
     * @throws ResourceNotFoundException if the car is not found
     * @throws InvalidFieldsException    if the photo upload fails
     */
    @PostMapping("/{carId}/upload-photo")
    public ResponseEntity<String> uploadCarPhoto(@PathVariable Long carId, @RequestParam("file") MultipartFile file) {
        carService.uploadCarPhoto(carId, file);
        return ResponseEntity.ok("Car photo uploaded successfully");
    }
}
