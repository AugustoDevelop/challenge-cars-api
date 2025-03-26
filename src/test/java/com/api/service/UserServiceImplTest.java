package com.api.service;

import com.api.dto.UserDto;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.helpers.UserDtoHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import com.api.util.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        carRepository.deleteAll();
        user = UsersHelper.createUsersEntity();
        userDto = UserDtoHelper.createUserDto();
    }

    @Test
    void testCreateUserValid() {
        User userExist = userService.createUser(userDto);
        assertNotNull(userExist);
    }

    @Test
    void testCreateUserMissingFields() {
        UserDto userDtoInvalid = UserDtoHelper.createUserDtoInvalid();
        assertThrows(MissingFieldsException.class, () -> userService.createUser(userDtoInvalid));
    }

    @Test
    void testCreateUserCombinatorialAnalysis() {
        List<String> fields = List.of("firstName", "lastName", "birthday", "password", "phone");

        for (int mask = 1; mask < (1 << fields.size()); mask++) {
            UserDto testDto = createTestUserDto(userDto, fields, mask);

            assertThrows(MissingFieldsException.class, () -> userService.createUser(testDto));
        }
    }


    @Test
    void testCreateUserEmailAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto.setEmail(existingUser.getEmail());

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testCreateUserLoginAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto.setLogin(existingUser.getLogin());

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }


    @Test
    void testGetUserByIdExisting() {
        User userExist = userRepository.save(this.user);
        User foundUser = userService.getUserById(userExist.getId());
        assertNotNull(foundUser);
    }

    @Test
    void testGetUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testDeleteUserByIdSuccess() {
        User userExist = userRepository.save(this.user);
        assertDoesNotThrow(() -> userService.deleteUserById(userExist.getId()));
        assertFalse(userRepository.findByIdAndStatus(userExist.getId(), UserStatus.ACTIVE).isPresent());
    }

    @Test
    void testDeleteUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(999L));
    }

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

    @Test
    void testUpdateUserNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(999L, userDto));
    }

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
