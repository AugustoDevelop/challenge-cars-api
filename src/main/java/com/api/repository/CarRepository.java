package com.api.repository;

import com.api.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Car entities, extending Spring Data JPA's JpaRepository for basic CRUD operations.
 *
 * <p>This interface provides additional custom query methods for retrieving cars based on specific criteria.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    /**
     * Retrieves a car by its unique license plate.
     *
     * @param licensePlate the license plate of the car to find
     * @return an Optional containing the found car, or empty if no car matches the given license plate
     */
    Optional<Car> findByLicensePlate(String licensePlate);

    /**
     * Retrieves a list of cars owned by a user with the specified login.
     *
     * @param login the login of the user whose cars to find
     * @return a list of cars owned by the user with the specified login
     */
    List<Car> findByUser_Login(String login);

    /**
     * Retrieves a car by its unique identifier and the login of the user who owns it.
     *
     * @param id    the ID of the car to find
     * @param login the login of the user who owns the car
     * @return an Optional containing the found car, or empty if no car matches the given criteria
     */
    Optional<Car> findByIdAndUser_Login(Long id, String login);
}
