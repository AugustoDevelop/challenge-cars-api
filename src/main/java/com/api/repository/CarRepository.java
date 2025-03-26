package com.api.repository;

import com.api.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Car entities.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    /**
     * Finds a car by its license plate.
     *
     * @param licensePlate the license plate of the car
     * @return an Optional containing the found car, or empty if no car was found
     */
    Optional<Car> findByLicensePlate(String licensePlate);
}
