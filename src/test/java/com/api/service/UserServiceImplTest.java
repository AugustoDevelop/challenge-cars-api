package com.api.service;

import com.api.dto.UserDto;
import com.api.entity.Users;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.helpers.UserDtoHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    private Users users;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        carRepository.deleteAll();
        users = UsersHelper.createUsersEntity();
        userDto = UserDtoHelper.createUserDto();
    }

    @Test
    void testCreateUserValid() {
        Users user = userService.createUser(userDto);
        assertNotNull(user);
    }

    @Test
    void testCreateUserMissingFields() {
        UserDto userDtoInvalid = UserDtoHelper.createUserDtoInvalid();
        assertThrows(MissingFieldsException.class, () -> userService.createUser(userDtoInvalid));
    }

    @Test
    void testCreateUserEmailAlreadyExists() {
        Users existingUser = userRepository.save(users);
        userDto.setEmail(existingUser.getEmail());

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testCreateUserLoginAlreadyExists() {
        Users existingUser = userRepository.save(users);
        userDto.setLogin(existingUser.getLogin());

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDto));
    }


    @Test
    void testGetUserByIdExisting() {
        Users user = userRepository.save(users);
        Users foundUser = userService.getUserById(user.getId());
        assertNotNull(foundUser);
    }

    @Test
    void testGetUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testDeleteUserByIdSuccess() {
        Users user = userRepository.save(users);
        assertDoesNotThrow(() -> userService.deleteUserById(user.getId()));
        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    void testDeleteUserByIdNonExisting() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(999L));
    }

    @Test
    void testUpdateUserSuccess() {
        Users user = userRepository.save(users);
        UserDto userUpdates = UserDtoHelper.createUserDto();
        userUpdates.setFirstName("Update first name");
        userUpdates.setLastName("Update last name");

        Users updatedUser = userService.updateUser(user.getId(), userUpdates);
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
        Users existingUser1 = userRepository.save(users);
        Users existingUser2 = new Users();
        existingUser2.setFirstName("João");
        existingUser2.setLastName("Silva");
        existingUser2.setBirthday("2024-01-01");
        existingUser2.setLogin("joao.silva22");
        existingUser2.setPassword("senha123");
        existingUser2.setEmail("joao.silva22@example.com");
        existingUser2.setPhone("123456789");
        userRepository.save(existingUser2);
        userDto.setEmail(existingUser2.getEmail());

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(existingUser1.getId(), userDto)
        );
    }

}
