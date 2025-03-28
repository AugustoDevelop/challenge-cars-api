package com.api.service;


import com.api.dto.UserDto;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for validating user data transfer objects and ensuring data consistency.
 *
 * <p>This class provides methods to check for missing fields, duplicate resources, and updates to user details.
 */
@Service
@AllArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;

    /**
     * Validates the user data transfer object for missing fields and duplicate resources.
     *
     * @param userDto the user data transfer object
     * @throws MissingFieldsException     if any required fields are missing
     * @throws DuplicateResourceException if the email or login already exists
     */
    public void validateUserDto(UserDto userDto) {
        if (userDto.firstName().isBlank() ||
            userDto.lastName().isBlank() ||
            userDto.birthday().isBlank() ||
            userDto.password().isBlank() ||
            userDto.phone().isBlank() ||
            userDto.email().isBlank() ||
            userDto.login().isBlank()
        ) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }

        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findByLogin(userDto.login()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessages.LOGIN_ALREADY_EXISTS);
        }
    }

    /**
     * Validates the email and login for uniqueness when updating an existing user.
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
}
