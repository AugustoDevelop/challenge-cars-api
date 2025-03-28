package com.api.service;

import com.api.dto.UserDto;
import com.api.dto.UserResponseDto;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.ResourceNotFoundException;
import com.api.helpers.CarHelper;
import com.api.helpers.UserDtoHelper;
import com.api.helpers.UserResponseDtoHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import com.api.util.UserStatus;
import com.api.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the UserServiceImpl, covering various scenarios such as creation, retrieval, update, and deletion of users.
 *
 * <p>This test class ensures that the UserServiceImpl behaves as expected under different conditions, including valid and invalid inputs.
 */
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    /**
     * Service for managing user-related operations.
     */
    private UserServiceImpl userService;


    /**
     * Repository for managing User entities.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Repository for managing Car entities.
     */
    @Mock
    private CarRepository carRepository;

    /**
     * Service for validating user data.
     */
    @Mock
    private UserValidationService userValidationService;

    /**
     * Service for sorting car data.
     */
    @Mock
    private CarSortingService carSortingService;

    /**
     * Mapper for converting between User DTOs and User entities.
     */
    @Mock
    private ModelMapper modelMapper;

    /**
     * Mapper for converting between User DTOs and User entities.
     */
    @Mock
    private UserMapper userMapper;

    /**
     * User entity used in tests.
     */
    private User user;

    /**
     * Data Transfer Object for User used in tests.
     */
    private UserDto userDto;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        userValidationService = mock(UserValidationService.class);
        carSortingService = mock(CarSortingService.class);
        userRepository = mock(UserRepository.class);
        carRepository = mock(CarRepository.class);
        modelMapper = mock(ModelMapper.class);
        userMapper = mock(UserMapper.class);

        userService = new UserServiceImpl(
                userValidationService,
                carSortingService,
                userRepository,
                carRepository,
                modelMapper,
                userMapper
        );

        userRepository.deleteAll();
        carRepository.deleteAll();
        user = UsersHelper.createUsersEntity();
        userDto = UserDtoHelper.createUserDto();
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(UserResponseDtoHelper.createUserResponseDto());
        when(userMapper.mapUserDtoToUser(any())).thenReturn(user);

    }

    /**
     * Tests the creation of a valid user.
     */
    @Test
    void testCreateUserValid() {
        when(userRepository.save(any())).thenReturn(user);
        UserResponseDto createdUser = userService.createUser(userDto);
        assertNotNull(createdUser);
    }

    /**
     * Tests creating a user with an email that already exists.
     */
    @Test
    void testCreateUserThrowsDuplicateResourceException() {
        doThrow(DuplicateResourceException.class).when(userValidationService).validateUserDto(userDto);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }

    /**
     * Tests creating a user with a login that already exists.
     */
    @Test
    void testCreateUserLoginAlreadyExists() {
        doThrow(DuplicateResourceException.class).when(userValidationService).validateUserDto(userDto);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }

    /**
     * Tests retrieving a user by ID when the ID exists.
     */
    @Test
    void testGetUserByIdExisting() {
        Random random = new Random();
        int size = random.nextInt(11);
        user.setCars(CarHelper.createCarList(size));
        when(userRepository.findByIdAndStatus(anyLong(), eq(UserStatus.ACTIVE))).thenReturn(Optional.of(user));

        UserResponseDto foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
    }

    /**
     * Tests retrieving a non-existing user by ID.
     */
    @Test
    void testGetUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    /**
     * Tests deleting a user by ID successfully.
     */
    @Test
    void testDeleteUserByIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Tests deleting a non-existing user by ID.
     */
    @Test
    void testDeleteUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(999L));
    }

    /**
     * Tests updating a user's details successfully.
     */
    @Test
    void testUpdateUserSuccess() {
        when(userRepository.findByIdAndStatus(anyLong(), eq(UserStatus.ACTIVE))).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        UserResponseDto updatedUser = userService.updateUser(1L, userDto);
        assertNotNull(updatedUser);
    }

    /**
     * Tests updating a non-existing user's details.
     */
    @Test
    void testUpdateUserNonExisting() {
        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(999L, userDto)
        );
    }

    /**
     * Tests updating a user's details with an email that already exists.
     */
    @Test
    void testUpdateUserEmailAlreadyExists() {
        User existingUser1 = UsersHelper.createUsersEntity();
        User existingUser2 = UsersHelper.createUsersEntity();
        existingUser2.setEmail("existing@example.com");
        userRepository.save(existingUser2);

        Long idToUpdate = existingUser1.getId();
        userDto = new UserDto(userDto.firstName(), userDto.lastName(), userDto.birthday(), userDto.login(), userDto.password(), "existing@example.com", userDto.phone(), userDto.cars());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(idToUpdate, userDto));
    }
}
