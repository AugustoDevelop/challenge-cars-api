package com.api.service;

import com.api.dto.UserDto;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.helpers.CarHelper;
import com.api.helpers.UserDtoHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import com.api.util.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserServiceImpl class.
 */
@SpringBootTest
class UserServiceImplTest {
    /**
     * The UserServiceImpl instance used for testing.
     */
    @Autowired
    private UserServiceImpl userService;

    /**
     * The UserRepository instance used for testing.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The CarRepository instance used for testing.
     */
    @Autowired
    private CarRepository carRepository;

    /**
     * The User entity used in tests.
     */
    private User user;

    /**
     * The UserDto used in tests.
     */
    private UserDto userDto;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        carRepository.deleteAll();
        user = UsersHelper.createUsersEntity();
        userDto = UserDtoHelper.createUserDto();
    }

    /**
     * Tests creating a valid user.
     */
    @Test
    void testCreateUserValid() {
        User userExist = userService.createUser(userDto);
        assertNotNull(userExist);
    }

    /**
     * Tests creating a user with missing fields.
     */
    @Test
    void testCreateUserMissingFields() {
        UserDto userDtoInvalid = UserDtoHelper.createUserDtoInvalid();
        assertThrows(MissingFieldsException.class, () -> userService.createUser(userDtoInvalid));
    }

    /**
     * Tests creating a user with combinatorial analysis of missing fields.
     */
    @Test
    void testCreateUserCombinatorialAnalysis() {
        List<String> fields = List.of("firstName", "lastName", "birthday", "password", "phone");

        for (int mask = 1; mask < (1 << fields.size()); mask++) {
            UserDto testDto = createTestUserDto(userDto, fields, mask);

            assertThrows(MissingFieldsException.class, () -> userService.createUser(testDto));
        }
    }

    /**
     * Tests creating a user when the email already exists.
     */
    @Test
    void testCreateUserEmailAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto.setEmail(existingUser.getEmail());

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }

    /**
     * Tests creating a user when the login already exists.
     */
    @Test
    void testCreateUserLoginAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto.setLogin(existingUser.getLogin());

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }

    /**
     * Tests retrieving an existing user by ID.
     */
    @Test
    @Transactional
    void testGetUserByIdExisting() {
        Random random = new Random();
        int size = random.nextInt(11);
        user.setCars(CarHelper.createCarList(size));
        User userExist = userRepository.save(this.user);
        User foundUser = userService.getUserById(userExist.getId());
        assertNotNull(foundUser);
        assertFalse(foundUser.getCars().isEmpty());
    }

    /**
     * Tests retrieving a non-existing user by ID.
     */
    @Test
    void testGetUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    /**
     * Tests deleting an existing user by ID.
     */
    @Test
    void testDeleteUserByIdSuccess() {
        User userExist = userRepository.save(this.user);
        assertDoesNotThrow(() -> userService.deleteUserById(userExist.getId()));
        assertFalse(userRepository.findByIdAndStatus(userExist.getId(), UserStatus.ACTIVE).isPresent());
    }

    /**
     * Tests deleting a non-existing user by ID.
     */
    @Test
    void testDeleteUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(999L));
    }

    /**
     * Tests updating an existing user successfully.
     */
    @Test
    void testUpdateUserSuccess() {
        User newUser = userRepository.save(this.user);
        UserDto userUpdates = UserDtoHelper.createUserDto();
        userUpdates.setFirstName("Update first name");
        userUpdates.setLastName("Update last name");

        User updatedUser = userService.updateUser(newUser.getId(), userUpdates);
        assertNotNull(updatedUser);
        assertEquals("Update first name", updatedUser.getFirstName());
        assertEquals("Update last name", updatedUser.getLastName());
    }

    /**
     * Tests updating a non-existing user.
     */
    @Test
    void testUpdateUserNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(999L, userDto));
    }

    /**
     * Tests updating a user when the email already exists.
     */
    @Test
    void testUpdateUserEmailAlreadyExists() {
        User existingUser1 = userRepository.save(user);
        User existingUser2 = new User();
        existingUser2.setFirstName("João");
        existingUser2.setLastName("Silva");
        existingUser2.setBirthday("2024-01-01");
        existingUser2.setLogin("joao.silva22");
        existingUser2.setPassword("senha123");
        existingUser2.setEmail("joao.silva22@example.com");
        existingUser2.setPhone("123456789");
        userRepository.save(existingUser2);
        userDto.setEmail(existingUser2.getEmail());
        Long userId = existingUser1.getId();
        assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(userId, userDto)
        );
    }

    /**
     * Creates a test UserDto with specified fields cleared based on the mask.
     *
     * @param baseUserDto the base UserDto
     * @param fields the list of fields
     * @param mask the mask indicating which fields to clear
     * @return the test UserDto
     */
    private UserDto createTestUserDto(UserDto baseUserDto, List<String> fields, int mask) {
        UserDto testDto = new UserDto();
        testDto.setEmail(baseUserDto.getEmail());
        testDto.setLogin(baseUserDto.getLogin());

        for (int i = 0; i < fields.size(); i++) {
            if ((mask & (1 << i)) != 0) {
                clearField(testDto, fields.get(i));
            } else {
                setValidField(testDto, baseUserDto, fields.get(i));
            }
        }

        return testDto;
    }

    /**
     * Clears the specified field in the UserDto.
     *
     * @param testDto the UserDto to modify
     * @param fieldName the name of the field to clear
     */
    private void clearField(UserDto testDto, String fieldName) {
        switch (fieldName) {
            case "firstName" -> testDto.setFirstName("");
            case "lastName" -> testDto.setLastName("");
            case "birthday" -> testDto.setBirthday("");
            case "password" -> testDto.setPassword("");
            case "phone" -> testDto.setPhone("");
            default -> throw new UnsupportedOperationException("Campo não suportado: " + fieldName);
        }
    }

    /**
     * Sets the specified field in the UserDto to a valid value.
     *
     * @param testDto the UserDto to modify
     * @param baseUserDto the base UserDto
     * @param fieldName the name of the field to set
     */
    private void setValidField(UserDto testDto, UserDto baseUserDto, String fieldName) {
        switch (fieldName) {
            case "firstName" -> testDto.setFirstName(baseUserDto.getFirstName());
            case "lastName" -> testDto.setLastName(baseUserDto.getLastName());
            case "birthday" -> testDto.setBirthday(baseUserDto.getBirthday());
            case "password" -> testDto.setPassword(baseUserDto.getPassword());
            case "phone" -> testDto.setPhone(baseUserDto.getPhone());
            default -> throw new UnsupportedOperationException("Campo não suportado: " + fieldName);
        }
    }

}
