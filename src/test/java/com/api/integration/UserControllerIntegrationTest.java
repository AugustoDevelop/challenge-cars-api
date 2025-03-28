package com.api.integration;

import com.api.dto.CarDto;
import com.api.dto.UserDto;
import com.api.dto.UserResponseDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.helpers.CarDtoHelper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the UserController, covering various scenarios such as creation, retrieval, deletion, and update of users.
 *
 * <p>This test class ensures that the UserController behaves as expected under different conditions, including valid and invalid inputs.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
     * Repository for managing Car entities.
     */
    @Autowired
    private CarRepository carRepository;

    /**
     * Service interface for User operations.
     */
    @Autowired
    private UserServiceInterface userService;

    /**
     * User data transfer object.
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
        carRepository.deleteAll();
        userDto = UserDtoHelper.createUserDto();
        user = UsersHelper.createUsersEntity();
    }

    /**
     * Tests the creation of a valid user.
     */
    @Test
    void testCreateUserValid() {
        ResponseEntity<User> response = restTemplate.postForEntity("/api/users/create", userDto, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * Tests the creation of a user with a missing field.
     *
     * @param fieldName    the name of the missing field
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

        userDtoInvalid = switch (fieldName) {
            case "firstName" ->
                    new UserDto(invalidValue, userDtoInvalid.lastName(), userDtoInvalid.birthday(), userDtoInvalid.login(), userDtoInvalid.password(), userDtoInvalid.email(), userDtoInvalid.phone(), userDtoInvalid.cars());
            case "lastName" ->
                    new UserDto(userDtoInvalid.firstName(), invalidValue, userDtoInvalid.birthday(), userDtoInvalid.login(), userDtoInvalid.password(), userDtoInvalid.email(), userDtoInvalid.phone(), userDtoInvalid.cars());
            case "birthday" ->
                    new UserDto(userDtoInvalid.firstName(), userDtoInvalid.lastName(), invalidValue, userDtoInvalid.login(), userDtoInvalid.password(), userDtoInvalid.email(), userDtoInvalid.phone(), userDtoInvalid.cars());
            case "password" ->
                    new UserDto(userDtoInvalid.firstName(), userDtoInvalid.lastName(), userDtoInvalid.birthday(), userDtoInvalid.login(), invalidValue, userDtoInvalid.email(), userDtoInvalid.phone(), userDtoInvalid.cars());
            case "phone" ->
                    new UserDto(userDtoInvalid.firstName(), userDtoInvalid.lastName(), userDtoInvalid.birthday(), userDtoInvalid.login(), userDtoInvalid.password(), userDtoInvalid.email(), invalidValue, userDtoInvalid.cars());
            default -> throw new IllegalArgumentException("Unexpected field: " + fieldName);
        };

        ResponseEntity<String> response = restTemplate.postForEntity("/api/users/create", userDtoInvalid, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests creating a user with an email that already exists.
     */
    @Test
    void testCreateUserEmailAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto = new UserDto(userDto.firstName(), userDto.lastName(), userDto.birthday(), userDto.login(), userDto.password(), existingUser.getEmail(), userDto.phone(), userDto.cars());

        ResponseEntity<String> response = restTemplate.postForEntity("/api/users/create", userDto, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests creating a user with a login that already exists.
     */
    @Test
    void testCreateUserLoginAlreadyExists() {
        User existingUser = userRepository.save(user);
        userDto = new UserDto(userDto.firstName(), userDto.lastName(), userDto.birthday(), existingUser.getLogin(), userDto.password(), userDto.email(), userDto.phone(), userDto.cars());

        ResponseEntity<String> response = restTemplate.postForEntity("/api/users/create", userDto, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests retrieving all users successfully.
     */
    @Test
    void testGetAllUsersSuccess() {
        userRepository.save(user);
        userRepository.save(UsersHelper.createUsersEntity());

        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    /**
     * Tests retrieving all users when the list is empty.
     */
    @Test
    void testGetAllUsersEmptyList() {
        userRepository.deleteAll();

        ResponseEntity<List<User>> response = restTemplate.exchange(
                "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

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

        assertNotNull(userRepository.save(this.user));

        ResponseEntity<UserResponseDto> response = restTemplate.exchange(
                "/api/users/" + userExist.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userExist.getFirstName(), response.getBody().firstName());
        assertEquals(userExist.getLastName(), response.getBody().lastName());
    }

    /**
     * Tests retrieving a user by a non-existing ID.
     */
    @Test
    void testGetUserByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/999", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests deleting a user by ID successfully.
     */
    @Test
    void testDeleteUserByIdSuccess() {
        User newUser = userRepository.save(this.user);

        ResponseEntity<Void> response = restTemplate.exchange("/api/users/" + newUser.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Tests deleting a user by a non-existing ID.
     */
    @Test
    void testDeleteUserByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.exchange("/api/users/999", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests updating a user.
     */
    @Test
    void testUpdateUser() {
        User existingUser = userRepository.save(user);

        UserDto updatedUserDto = new UserDto(
                "João Jose",
                "Silva Nune",
                "2024-01-01",
                "joao.silva.nune",
                "senha1231",
                "joao.silva2@example.com",
                "123456789",
                List.of()
        );

        HttpEntity<UserDto> entity = createJsonEntity(updatedUserDto);
        ResponseEntity<User> updateResponse = restTemplate.exchange(
                "/api/users/" + existingUser.getId(),
                HttpMethod.PUT,
                entity,
                User.class
        );
        assertNotNull(updateResponse);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    /**
     * Tests updating a user with non-existing cars.
     */
    @Test
    void testUpdateUserCarsNonExistingCar() {
        User newUser = userRepository.save(this.user);
        CarDto carUpdate = CarDtoHelper.createCarDto();
        userDto = new UserDto(
                "JOSE",
                userDto.lastName(),
                userDto.birthday(),
                userDto.login(),
                userDto.password(),
                userDto.email(),
                userDto.phone(),
                List.of(carUpdate)
        );

        UserResponseDto updatedUser = userService.updateUser(newUser.getId(), userDto);
        assertNotNull(updatedUser.cars());
        assertEquals(2, updatedUser.cars().size());
    }

    /**
     * Tests updating a user with duplicate license plates for cars.
     */
    @Test
    void testUpdateUserCarsDuplicateLicensePlate() {
        User newUser = userRepository.save(this.user);
        Car existingCar = carRepository.save(newUser.getCars().get(0));
        CarDto carUpdate = CarDtoHelper.createCarDto();

        carUpdate = new CarDto(
                carUpdate.year(),
                existingCar.getLicensePlate(),
                carUpdate.model(),
                carUpdate.color()

        );

        UserDto newUserDto = new UserDto(
                userDto.firstName(),
                userDto.lastName(),
                userDto.birthday(),
                userDto.login(),
                userDto.password(),
                userDto.email(),
                userDto.phone(),
                List.of(carUpdate)
        );

        UserResponseDto updatedUser = userService.updateUser(newUser.getId(), newUserDto);
        assertNotNull(updatedUser.cars());
        assertEquals(1, updatedUser.cars().size());
        assertEquals(carUpdate.color(), updatedUser.cars().get(0).color());
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

