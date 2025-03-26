package com.api.integration;

import com.api.dto.UserDto;
import com.api.entity.Users;
import com.api.helpers.UserDtoHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void testCreateUserMissingFields() {
        UserDto userDtoInvalid = UserDtoHelper.createUserDtoInvalid();

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

}
