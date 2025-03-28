package com.api.service;

import com.api.dto.UserDto;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidationService userValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateUserDto_shouldThrowMissingFieldsException_whenFieldsAreMissing() {
        UserDto userDto = new UserDto("", "", "", "", "", "", "", List.of());

        assertThrows(MissingFieldsException.class, () -> userValidationService.validateUserDto(userDto));
    }

    @Test
    void validateUserDto_shouldThrowDuplicateResourceException_whenEmailExists() {
        UserDto userDto = new UserDto("John", "Doe", "01-01-2000", "password", "1234567890", "existing@example.com", "johndoe", List.of());
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> userValidationService.validateUserDto(userDto));
    }

    @Test
    void validateUserDto_shouldThrowDuplicateResourceException_whenLoginExists() {
        UserDto userDto = new UserDto("John", "Doe", "01-01-2000", "password", "1234567890", "john@example.com", "existinglogin", List.of());
        when(userRepository.findByLogin(userDto.login())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> userValidationService.validateUserDto(userDto));
    }

    @Test
    void validateEmailAndLogin_shouldThrowDuplicateResourceException_whenEmailExists() {
        User existingUser = new User();
        existingUser.setEmail("old@example.com");
        User userUpdates = new User();
        userUpdates.setEmail("new@example.com");

        when(userRepository.findByEmail(userUpdates.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> userValidationService.validateEmailAndLogin(existingUser, userUpdates));
    }

    @Test
    void validateEmailAndLogin_shouldThrowDuplicateResourceException_whenLoginExists() {
        User existingUser = new User();
        existingUser.setLogin("oldlogin");
        User userUpdates = new User();
        userUpdates.setLogin("newlogin");

        when(userRepository.findByLogin(userUpdates.getLogin())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> userValidationService.validateEmailAndLogin(existingUser, userUpdates));
    }
}