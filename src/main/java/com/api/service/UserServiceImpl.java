package com.api.service;

import com.api.dto.CarDto;
import com.api.dto.UserDto;
import com.api.dto.UserResponseDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.InvalidFieldsException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.UserServiceInterface;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import com.api.util.UserStatus;
import com.api.util.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing User entities, providing methods for CRUD operations and photo uploads.
 *
 * <p>This class encapsulates the business logic for user management, ensuring validation and consistency of user data.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServiceInterface {
    private final UserValidationService userValidationService;
    private final CarSortingService carSortingService;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    /**
     * Creates a new user based on the provided data transfer object.
     *
     * @param userDto the user data transfer object
     * @return the created user
     * @throws MissingFieldsException     if any required fields are missing
     * @throws DuplicateResourceException if the email or login already exists
     */
    public UserResponseDto createUser(UserDto userDto) {
        userValidationService.validateUserDto(userDto);
        User user = userMapper.mapUserDtoToUser(userDto);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDto.class);
    }

    /**
     * Retrieves all active users and sorts their cars by usage amount in descending order.
     *
     * @return a list of active users with their cars sorted by usage amount
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findUsersByStatus(UserStatus.ACTIVE);
        users.forEach(carSortingService::sortCarsByUsageAmount);

        if (users.isEmpty()) {
            return new ArrayList<>();
        } else {
            return users.stream()
                    .map(user -> modelMapper.map(user, UserResponseDto.class))
                    .toList();
        }
    }

    /**
     * Retrieves a user by ID and sorts their cars by usage amount in descending order.
     *
     * @param id the user ID
     * @return the user with their cars sorted by usage amount
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        carSortingService.sortCarsByUsageAmount(user);
        return modelMapper.map(user, UserResponseDto.class);
    }

    /**
     * Deletes a user by ID (logical delete by setting the user status to inactive).
     *
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    /**
     * Uploads a photo for a specific user.
     *
     * @param userId the user ID
     * @param file   the photo file
     * @throws ResourceNotFoundException if the user is not found
     * @throws InvalidFieldsException    if the photo upload fails
     */
    public UserResponseDto uploadUserPhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get("uploads", "users", String.valueOf(userId), file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            user.setPhotoProfileUrl(path.toString());
            User newUser = userRepository.save(user);
            return modelMapper.map(newUser, UserResponseDto.class);
        } catch (IOException e) {
            throw new InvalidFieldsException(ErrorMessages.INVALID_PHOTO);
        }
    }

    /**
     * Updates an existing user with the provided updated details.
     *
     * @param id          the user ID
     * @param userUpdates the user data transfer object with updates
     * @return the updated user
     * @throws ResourceNotFoundException  if the user is not found
     * @throws DuplicateResourceException if the email or login already exists
     */
    @Override
    public UserResponseDto updateUser(Long id, UserDto userUpdates) {
        User existingUser = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));

        User userUpdatesMapped = modelMapper.map(userUpdates, User.class);
        userValidationService.validateEmailAndLogin(existingUser, userUpdatesMapped);

        existingUser.setFirstName(userUpdates.firstName());
        existingUser.setLastName(userUpdates.lastName());
        existingUser.setBirthday(userUpdates.birthday());
        existingUser.setLogin(userUpdates.login());
        existingUser.setPassword(userUpdates.password());
        existingUser.setEmail(userUpdates.email());
        existingUser.setPhone(userUpdates.phone());

        if (userUpdates.cars() != null) {
            // Update existing cars
            for (CarDto carUpdate : userUpdates.cars()) {
                boolean carExists = false;
                for (Car existingCar : existingUser.getCars()) {
                    if (existingCar.getLicensePlate().equals(carUpdate.licensePlate())) {
                        existingCar.setYear(carUpdate.year());
                        existingCar.setModel(carUpdate.model());
                        existingCar.setColor(carUpdate.color());
                        carExists = true;
                        break;
                    }
                }
                // Add new cars
                if (!carExists) {
                    Optional<Car> existingCarOpt = carRepository.findByLicensePlate(carUpdate.licensePlate());
                    if (existingCarOpt.isPresent()) {
                        Car existingCar = existingCarOpt.get();
                        if (!existingCar.getUser().getId().equals(existingUser.getId())) {
                            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
                        }
                    } else {
                        Car newCar = new Car();
                        newCar.setLicensePlate(carUpdate.licensePlate());
                        newCar.setYear(carUpdate.year());
                        newCar.setModel(carUpdate.model());
                        newCar.setColor(carUpdate.color());
                        newCar.setUser(existingUser);
                        existingUser.getCars().add(newCar);
                    }
                }
            }
        }

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserResponseDto.class);
    }
}
