package com.api.integration;

import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.Users;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserServiceInterface userService;

    private UserDto userDto;
    private Users users;


    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userDto = UserDtoHelper.createUserDto();
        users = UsersHelper.createUsersEntity();
    }


    @Test
    void testCreateUserValid() {
        ResponseEntity<Users> response = restTemplate.postForEntity("/users/create", userDto, Users.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

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
        }

        ResponseEntity<String> response = restTemplate.postForEntity("/users/create", userDtoInvalid, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateUserEmailAlreadyExists() {
        Users existingUser = userRepository.save(users);
        userDto.setEmail(existingUser.getEmail());

        ResponseEntity<String> response = restTemplate.postForEntity("/users/create", userDto, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testCreateUserLoginAlreadyExists() {
        Users existingUser = userRepository.save(users);
        userDto.setLogin(existingUser.getLogin());

        ResponseEntity<String> response = restTemplate.postForEntity("/users/create", userDto, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testGetAllUsersSuccess() {
        users.setLogin("Login");
        users.setEmail("Email");
        Users user1 = userRepository.save(users);
        Users user2 = userRepository.save(UsersHelper.createUsersEntity());
        user2.setFirstName("Outro Nome");

        ResponseEntity<List<Users>> response = restTemplate.exchange("/users", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(response.getBody().stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    @Test
    void testGetAllUsersEmptyList() {
        ResponseEntity<List<Users>> response = restTemplate.exchange("/users", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetUserByIdSuccess() {
        Users user = userRepository.save(users);

        ResponseEntity<Users> response = restTemplate.getForEntity("/users/" + user.getId(), Users.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals(user.getFirstName(), response.getBody().getFirstName());
        assertEquals(user.getLastName(), response.getBody().getLastName());
    }

    @Test
    void testGetUserByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.getForEntity("/users/999", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteUserByIdSuccess() {
        Users user = userRepository.save(users);

        ResponseEntity<Void> response = restTemplate.exchange("/users/" + user.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    void testDeleteUserByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.exchange("/users/999", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateUser() {
        Users existingUser = userRepository.save(users);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setFirstName("João Jose");
        updatedUserDto.setLastName("Silva Nune");
        updatedUserDto.setBirthday("2024-01-01");
        updatedUserDto.setLogin("joao.silva.nune");
        updatedUserDto.setPassword("senha1231");
        updatedUserDto.setEmail("joao.silva2@example.com");
        updatedUserDto.setPhone("123456789");

        HttpEntity<UserDto> entity = createJsonEntity(updatedUserDto);
        ResponseEntity<Users> updateResponse = restTemplate.exchange("/users/" + existingUser.getId(), HttpMethod.PUT, entity, Users.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        assertEquals("João Jose", Objects.requireNonNull(updateResponse.getBody()).getFirstName());
        assertEquals("Silva Nune", updateResponse.getBody().getLastName());
        assertEquals("2024-01-01", updateResponse.getBody().getBirthday());
        assertEquals("joao.silva.nune", updateResponse.getBody().getLogin());
        assertEquals("senha1231", updateResponse.getBody().getPassword());
        assertEquals("joao.silva2@example.com", updateResponse.getBody().getEmail());
        assertEquals("123456789", updateResponse.getBody().getPhone());
    }

    @Test
    void testUpdateUserLoginAlreadyExists() {
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
        userDto.setLogin(existingUser2.getLogin());

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(existingUser1.getId(), userDto)
        );
    }

    @Test
    void testUpdateUserCarsNonExistingCar() {
        Users user = userRepository.save(users);
        Car carUpdate = CarHelper.createCar();
        userDto.setCars(List.of(carUpdate));

        Users updatedUser = userService.updateUser(user.getId(), userDto);
        assertNotNull(updatedUser.getCars());
        assertEquals(1, updatedUser.getCars().size());
    }

    @Test
    void testUpdateUserCarsDuplicateLicensePlate() {
        Users user = userRepository.save(users);
        Car existingCar = carRepository.save(CarHelper.createCar());
        Car carUpdate = CarHelper.createCar();
        carUpdate.setLicensePlate(existingCar.getLicensePlate());
        userDto.setCars(List.of(carUpdate));

        // Não deve lançar exceção aqui, pois a lógica atual não verifica duplicatas de placa em update
        Users updatedUser = userService.updateUser(user.getId(), userDto);
        assertNotNull(updatedUser.getCars());
        assertEquals(1, updatedUser.getCars().size());
    }


    private HttpEntity<UserDto> createJsonEntity(UserDto userDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(userDto, headers);
    }

}
