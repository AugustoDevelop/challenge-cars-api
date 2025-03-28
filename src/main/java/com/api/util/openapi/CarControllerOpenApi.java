package com.api.util.openapi;

import com.api.dto.CarDto;
import com.api.entity.Car;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for Car Controller OpenAPI documentation.
 */
@Tag(name = "Car", description = "Operations related to cars")
public interface CarControllerOpenApi {

    /**
     * Creates a new car with the provided details.
     *
     * @param carDto the car details
     * @return the created car
     */
    @Operation(summary = "Create a new car", description = "Creates a new car with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Car created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/api/cars")
    ResponseEntity<Car> createCar(@RequestBody @Valid CarDto carDto);

    /**
     * Retrieves a list of all cars.
     *
     * @return the list of cars
     */
    @Operation(summary = "Get all cars", description = "Retrieves a list of all cars")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of cars retrieved successfully")
    })
    @GetMapping("/api/cars")
    ResponseEntity<List<Car>> getAllCars();

    /**
     * Retrieves a car by its unique identifier.
     *
     * @param id the car ID
     * @return the car
     */
    @Operation(summary = "Get car by ID", description = "Retrieves a car by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/api/cars/{id}")
    ResponseEntity<Car> getCarById(@Parameter(description = "ID of the car to be retrieved") @PathVariable Long id);

    /**
     * Updates the details of an existing car.
     *
     * @param id     the car ID
     * @param carDto the car details
     * @return the updated car
     */
    @Operation(summary = "Update car details", description = "Updates the details of an existing car")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PutMapping("/api/cars/{id}")
    ResponseEntity<Car> updateCar(@Parameter(description = "ID of the car to be updated") @PathVariable Long id, @RequestBody @Valid CarDto carDto);

    /**
     * Deletes a car by its unique identifier.
     *
     * @param id the car ID
     * @return a response entity with no content
     */
    @Operation(summary = "Delete car by ID", description = "Deletes a car by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @DeleteMapping("/api/cars/{id}")
    ResponseEntity<Void> deleteCarById(@Parameter(description = "ID of the car to be deleted") @PathVariable Long id);

    /**
     * Uploads a photo for a specific car.
     *
     * @param carId the car ID
     * @param file  the photo file
     * @return a response entity with the upload status
     */
    @Operation(summary = "Upload car photo", description = "Uploads a photo for a specific car")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PostMapping("/api/cars/{carId}/upload-photo")
    ResponseEntity<String> uploadCarPhoto(@Parameter(description = "ID of the car to upload the photo for") @PathVariable Long carId, @RequestParam("file") MultipartFile file);
}