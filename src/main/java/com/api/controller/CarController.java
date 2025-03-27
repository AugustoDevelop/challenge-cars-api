package com.api.controller;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.interfaces.CarServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing cars.
 */
@RestController
@RequestMapping("/cars")
@AllArgsConstructor
public class CarController {

    private final CarServiceInterface carService;

    /**
     * Creates a new car.
     *
     * @param carDto the car data transfer object
     * @return the created car
     */
    @PostMapping("/create")
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
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car
     * @return the car with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    /**
     * Updates a car.
     *
     * @param id     the ID of the car to update
     * @param carDto the car data transfer object
     * @return the updated car
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody @Valid CarDto carDto) {
        Car updatedCar = carService.updateCar(id, carDto);
        return ResponseEntity.ok(updatedCar);
    }

    /**
     * Deletes a car.
     *
     * @param id the ID of the car to delete
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads a photo for a car.
     *
     * @param carId  the ID of the car
     * @param file   the photo file to upload
     * @return a response entity with a success message
     */
    @PostMapping("/{carId}/upload-photo")
    public ResponseEntity<String> uploadCarPhoto(@PathVariable Long carId, @RequestParam("file") MultipartFile file) {
        carService.uploadCarPhoto(carId, file);
        return ResponseEntity.ok("Car photo uploaded successfully");
    }
}
