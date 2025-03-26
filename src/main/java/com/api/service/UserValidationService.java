package com.api.service;


import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Validates the user data transfer object.
 */
@Service
@AllArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    /**
     * Validates the user data transfer object.
     *
     * @param userDto the user data transfer object
     * @throws MissingFieldsException     if any required fields are missing
     * @throws DuplicateResourceException if the email or login already exists
     */
    public void validateUserDto(UserDto userDto) {
        if (userDto.getFirstName().isBlank() ||
            userDto.getLastName().isBlank() ||
            userDto.getBirthday().isBlank() ||
            userDto.getPassword().isBlank() ||
            userDto.getPhone().isBlank()
        ) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findByLogin(userDto.getLogin()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessages.LOGIN_ALREADY_EXISTS);
        }
    }

    /**
     * Validates the email and login for uniqueness.
     *
     * @param existingUser the existing user
     * @param userUpdates  the user data transfer object with updates
     * @throws DuplicateResourceException if the email or login already exists
     */
    public void validateEmailAndLogin(User existingUser, User userUpdates) {
        if (
                userUpdates.getEmail() != null &&
                !userUpdates.getEmail().equals(existingUser.getEmail()) &&
                userRepository.findByEmail(userUpdates.getEmail()).isPresent()
        ) {
            throw new DuplicateResourceException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (
                userUpdates.getLogin() != null &&
                !userUpdates.getLogin().equals(existingUser.getLogin()) &&
                userRepository.findByLogin(userUpdates.getLogin()).isPresent()
        ) {
            throw new DuplicateResourceException(ErrorMessages.LOGIN_ALREADY_EXISTS);
        }
    }

    /**
     * Updates the cars associated with a user.
     *
     * @param existingUser the existing user
     * @param carsUpdates  the list of car updates
     */
    public void updateCars(User existingUser, List<Car> carsUpdates) {
        existingUser.setCars(new ArrayList<>());
        for (Car carUpdate : carsUpdates) {
            Car existingCar;

            if (carUpdate.getLicensePlate() == null) {
                throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
            } else {
                Optional<Car> optionalCar = carRepository.findByLicensePlate(carUpdate.getLicensePlate());
                existingCar = optionalCar.orElseGet(Car::new);
            }

            existingCar.setYear(carUpdate.getYear());
            existingCar.setLicensePlate(carUpdate.getLicensePlate());
            existingCar.setModel(carUpdate.getModel());
            existingCar.setColor(carUpdate.getColor());

            Car savedCar = carRepository.save(existingCar);

            existingUser.getCars().add(savedCar);
        }
    }
}
