package com.api.service;

import com.api.dto.UserDto;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.InvalidFieldsException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.UserServiceInterface;
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
import java.util.List;

    /**
     * Service implementation for managing users.
     */
    @Service
    @AllArgsConstructor
    public class UserServiceImpl implements UserServiceInterface {
        private final UserValidationService userValidationService;
        private final CarSortingService carSortingService;
        private final UserRepository userRepository;
        private final ModelMapper modelMapper;
        private final UserMapper userMapper;

        /**
         * Creates a new user.
         *
         * @param userDto the user data transfer object
         * @return the created user
         * @throws MissingFieldsException     if any required fields are missing
         * @throws DuplicateResourceException if the email or login already exists
         */
        public User createUser(UserDto userDto) {
            userValidationService.validateUserDto(userDto);
            User user = userMapper.mapUserDtoToUser(userDto);
            return userRepository.save(user);
        }

        /**
         * Retrieves all active users and sorts their cars by usage amount in descending order.
         *
         * @return a list of active users with their cars sorted by usage amount
         */
        @Override
        public List<User> getAllUsers() {
            List<User> users = userRepository.findByStatus(UserStatus.ACTIVE);
            users.forEach(carSortingService::sortCarsByUsageAmount);
            return users;
        }

        /**
         * Retrieves a user by ID and sorts their cars by usage amount in descending order.
         *
         * @param id the user ID
         * @return the user with their cars sorted by usage amount
         * @throws ResourceNotFoundException if the user is not found
         */
        @Override
        public User getUserById(Long id) {
            User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
            carSortingService.sortCarsByUsageAmount(user);
            return user;
        }

        /**
         * Deletes a user by ID (logical delete).
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
         * Uploads a photo for the user.
         *
         * @param userId the user ID
         * @param file   the photo file
         * @throws ResourceNotFoundException if the user is not found
         * @throws InvalidFieldsException    if the photo upload fails
         */
        public void uploadUserPhoto(Long userId, MultipartFile file) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get("uploads", "users", String.valueOf(userId), file.getOriginalFilename());
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
                user.setPhotoUrl(path.toString());
                userRepository.save(user);
            } catch (IOException e) {
                throw new InvalidFieldsException(ErrorMessages.INVALID_PHOTO);
            }
        }

        /**
         * Updates a user.
         *
         * @param id          the user ID
         * @param userUpdates the user data transfer object with updates
         * @return the updated user
         * @throws ResourceNotFoundException  if the user is not found
         * @throws DuplicateResourceException if the email or login already exists
         */
        public User updateUser(Long id, UserDto userUpdates) {
            User user = modelMapper.map(userUpdates, User.class);
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));

            userValidationService.validateEmailAndLogin(existingUser, user);

            existingUser.setFirstName(userUpdates.getFirstName());
            existingUser.setLastName(userUpdates.getLastName());
            existingUser.setBirthday(userUpdates.getBirthday());
            existingUser.setLogin(userUpdates.getLogin());
            existingUser.setPassword(userUpdates.getPassword());
            existingUser.setEmail(userUpdates.getEmail());
            existingUser.setPhone(userUpdates.getPhone());

            if (userUpdates.getCars() != null) {
                userValidationService.updateCars(existingUser, userUpdates.getCars());
            }

            return userRepository.save(existingUser);
        }
    }