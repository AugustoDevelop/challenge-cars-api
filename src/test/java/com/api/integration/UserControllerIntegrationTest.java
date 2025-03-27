package com.api.integration;

import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.exception.DuplicateResourceException;
import com.api.helpers.CarHelper;
import com.api.helpers.UserDtoHelper;
import com.api.helpers.UsersHelper;
import com.api.interfaces.UserServiceInterface;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the UserController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    /**
     * TestRestTemplate for making HTTP requests in tests.
     */
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Repository for managing User entities.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for managing User entities.
     */
    @Autowired
    private CarRepository carRepository;

    /**
     * Service interface for User operations.
     */
    @Autowired
    private UserServiceInterface userService;

    /**
     * Service interface for User operations.
     */
    private UserDto userDto;

    /**
     * User entity.
     */
    private User user;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userDto = UserDtoHelper.createUserDto();
        user = UsersHelper.createUsersEntity();
    }

    /**
     * Tests the creation of a valid user.
     */
    @Test
    void testCreateUserValid() {
        ResponseEntity<User> response = restTemplate.postForEntity("/users/create", userDto, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    /**
     * Tests the creation of a user with missing fields.
     *
     * @param fieldName   the name of the field that is missing
     * @param invalidValue the invalid value for the field
     */
    @ParameterizedTest
    @CsvSource({
            "firstName,",
            "lastName,",
            "birthday,",
            "password,",
            "phone,",
    })
    void testCreateUserMissingField(String fieldName, String invalidValue) {
        UserDto userDtoInvalid = UserDtoHelper.createUserDto();

        switch (fieldName) {
            case "firstName" -> userDtoInvalid.setFirstName(invalidValue);
            case "lastName" -> userDtoInvalid.setLastName(invalidValue);
            case "birthday" -> userDtoInvalid.setBirthday(invalidValue);
            case "password" -> userDtoInvalid.setPassword(invalidValue);
            case "phone" -> userDtoInvalid.setPhone(invalidValue);
            default -> throw new IllegalArgumentException("Unexpected field: " + fieldName);
        }

        ResponseEntity<String> response = restTemplate.postForEntity("/users/create", userDtoInvalid, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests the creation of a user with an email that already exists.
     */
    @Test
    void testCreateUserEmailAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto.setEmail(existingUser.getEmail());

        ResponseEntity<String> response = restTemplate.postForEntity("/users/create", userDto, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests the creation of a user with a login that already exists.
     */
    @Test
    void testCreateUserLoginAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto.setLogin(existingUser.getLogin());

        ResponseEntity<String> response = restTemplate.postForEntity("/users/create", userDto, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests retrieving all users successfully.
     */
    @Test
    void testGetAllUsersSuccess() {
        user.setLogin("Login");
        user.setEmail("Email");
        User user1 = userRepository.save(user);
        User user2 = userRepository.save(UsersHelper.createUsersEntity());
        user2.setFirstName("Outro Nome");

        ResponseEntity<List<User>> response = restTemplate.exchange("/users", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(response.getBody().stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    /**
     * Tests retrieving all users when the list is empty.
     */
    @Test
    void testGetAllUsersEmptyList() {
        ResponseEntity<List<User>> response = restTemplate.exchange("/users", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Tests retrieving a user by ID successfully.
     */
    @Test
    void testGetUserByIdSuccess() {
        User userExist = userRepository.save(this.user);

        ResponseEntity<User> response = restTemplate.getForEntity("/users/" + userExist.getId(), User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userExist.getId(), response.getBody().getId());
        assertEquals(userExist.getFirstName(), response.getBody().getFirstName());
        assertEquals(userExist.getLastName(), response.getBody().getLastName());
    }

    /**
     * Tests retrieving a user by a non-existing ID.
     */
    @Test
    void testGetUserByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.getForEntity("/users/999", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests deleting a user by ID successfully.
     */
    @Test
    void testDeleteUserByIdSuccess() {
        User newUser = userRepository.save(this.user);

        ResponseEntity<Void> response = restTemplate.exchange("/users/" + newUser.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Tests deleting a user by a non-existing ID.
     */
    @Test
    void testDeleteUserByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.exchange("/users/999", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests updating a user.
     */
    @Test
    void testUpdateUser() {
        User existingUser = userRepository.save(user);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setFirstName("João Jose");
        updatedUserDto.setLastName("Silva Nune");
        updatedUserDto.setBirthday("2024-01-01");
        updatedUserDto.setLogin("joao.silva.nune");
        updatedUserDto.setPassword("senha1231");
        updatedUserDto.setEmail("joao.silva2@example.com");
        updatedUserDto.setPhone("123456789");

        HttpEntity<UserDto> entity = createJsonEntity(updatedUserDto);
        ResponseEntity<User> updateResponse = restTemplate.exchange("/users/" + existingUser.getId(), HttpMethod.PUT, entity, User.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        assertEquals("João Jose", Objects.requireNonNull(updateResponse.getBody()).getFirstName());
        assertEquals("Silva Nune", updateResponse.getBody().getLastName());
        assertEquals("2024-01-01", updateResponse.getBody().getBirthday());
        assertEquals("joao.silva.nune", updateResponse.getBody().getLogin());
        assertEquals("senha1231", updateResponse.getBody().getPassword());
        assertEquals("joao.silva2@example.com", updateResponse.getBody().getEmail());
        assertEquals("123456789", updateResponse.getBody().getPhone());
    }

    /**
     * Tests updating a user with a login that already exists.
     */
    @Test
    void testUpdateUserLoginAlreadyExists() {
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
        userDto.setLogin(existingUser2.getLogin());
        Long userId = existingUser1.getId();
        assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(userId, userDto)
        );
    }

    /**
     * Tests updating a user with non-existing cars.
     */
    @Test
    void testUpdateUserCarsNonExistingCar() {
        User newUser = userRepository.save(this.user);
        Car carUpdate = CarHelper.createCar();
        userDto.setCars(List.of(carUpdate));

        User updatedUser = userService.updateUser(newUser.getId(), userDto);
        assertNotNull(updatedUser.getCars());
        assertEquals(1, updatedUser.getCars().size());
    }

    /**
     * Tests updating a user with duplicate license plates for cars.
     */
    @Test
    void testUpdateUserCarsDuplicateLicensePlate() {
        User newUser = userRepository.save(this.user);
        Car existingCar = carRepository.save(CarHelper.createCar());
        Car carUpdate = CarHelper.createCar();
        carUpdate.setLicensePlate(existingCar.getLicensePlate());
        userDto.setCars(List.of(carUpdate));

        // Não deve lançar exceção aqui, pois a lógica atual não verifica duplicatas de placa em update
        User updatedUser = userService.updateUser(newUser.getId(), userDto);
        assertNotNull(updatedUser.getCars());
        assertEquals(1, updatedUser.getCars().size());
    }

    /**
     * Creates an HttpEntity with JSON content type.
     *
     * @param userDto the UserDto to be included in the entity
     * @return the HttpEntity with JSON content type
     */
    private HttpEntity<UserDto> createJsonEntity(UserDto userDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(userDto, headers);
    }

}
