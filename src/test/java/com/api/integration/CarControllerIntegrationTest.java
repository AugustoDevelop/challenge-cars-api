package com.api.integration;

import com.api.config.TokenService;
import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.helpers.CarDtoHelper;
import com.api.helpers.CarHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the CarController, covering various scenarios such as creation, retrieval, deletion, and update of cars.
 *
 * <p>This test class ensures that the CarController behaves as expected under different conditions, including valid and invalid inputs.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CarControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private Car car;
    private User user;
    private CarDto carDto;

    @BeforeEach
    void setUp() {
        user = UsersHelper.createUsersEntity();
        car = CarHelper.createCar();
        carDto = CarDtoHelper.createCarDto();
        carRepository.deleteAll();

        User existingUser = userRepository.save(user);

        // Create an authentication token and set it in the SecurityContext
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(existingUser.getEmail(), existingUser.getPassword(), new ArrayList<>());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newToken = tokenService.generateToken(existingUser);
        restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + newToken);
            return execution.execute(request, body);
        });
    }

    /**
     * Tests creating a car with valid data.
     */
    @Test
    void testCreateCarSuccess() {
        ResponseEntity<Car> response = restTemplate.postForEntity(
                "/api/cars",
                carDto,
                Car.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    /**
     * Tests creating a car with missing fields.
     */
    @Test
    void testCreateCarMissingFields() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/cars", CarDtoHelper.createCarDtoInvalid(), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests creating a car with an existing license plate.
     */
    @Test
    void testCreateCarLicensePlateAlreadyExists() {
        Car existingCar = carRepository.save(car);
        carDto = new CarDto(carDto.year(), existingCar.getLicensePlate(), carDto.model(), carDto.color());

        ResponseEntity<String> response = restTemplate.postForEntity("/api/cars", carDto, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests retrieving all cars successfully.
     */
    @Test
    void testGetAllCarsSuccess() {
        Car existingCar = CarHelper.createCar();
        car.setLicensePlate("NEW");
        existingCar.setUser(user);
        carRepository.save(car);
        carRepository.save(existingCar);

        ResponseEntity<List<Car>> response = restTemplate.exchange("/api/cars", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    /**
     * Tests retrieving all cars when the list is empty.
     */
    @Test
    void testGetAllCarsEmptyList() {
        carRepository.deleteAll();

        ResponseEntity<List<Car>> response = restTemplate.exchange("/api/cars", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Tests retrieving a car by ID successfully.
     */
    @Test
    void testGetCarByIdSuccess() {
        Car createCar = CarHelper.createCar();
        createCar.setUser(user);
        Car newCar = carRepository.save(createCar);

        ResponseEntity<Car> response = restTemplate.getForEntity("/api/cars/" + newCar.getId(), Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(newCar.getId(), response.getBody().getId());
        assertEquals(newCar.getLicensePlate(), response.getBody().getLicensePlate());
    }

    /**
     * Tests retrieving a non-existent car by ID.
     */
    @Test
    void testGetCarByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/cars/999", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests deleting a car by ID successfully.
     */
    @Test
    void testDeleteCarByIdSuccess() {
        Car createCar = CarHelper.createCar();
        createCar.setUser(user);
        Car newCar = carRepository.save(createCar);

        ResponseEntity<Void> response = restTemplate.exchange("/api/cars/" + newCar.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(carRepository.existsById(newCar.getId()));
    }

    /**
     * Tests deleting a non-existent car by ID.
     */
    @Test
    void testDeleteCarByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.exchange("/api/cars/999", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests updating a car successfully.
     */
    @Test
    void testUpdateCarSuccess() {
        Car createCar = CarHelper.createCar();
        createCar.setUser(user);
        Car newCar = carRepository.save(createCar);
        
        carDto = new CarDto(2022, carDto.licensePlate(), "Novo Modelo", "Nova Cor");

        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<Car> response = restTemplate.exchange("/api/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(carDto.year(), response.getBody().getYear());
        assertEquals(carDto.licensePlate(), response.getBody().getLicensePlate());
        assertEquals(carDto.model(), response.getBody().getModel());
        assertEquals(carDto.color(), response.getBody().getColor());
    }

    /**
     * Tests updating a car with an invalid license plate.
     */
    @Test
    void testUpdateCarInvalidLicensePlate() {
        Car newCar = carRepository.save(car);
        carDto = new CarDto(carDto.year(), "PLACA INVALIDA", carDto.model(), carDto.color());
        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<String> response = restTemplate.exchange("/api/cars/" + newCar.getId(), HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests updating a car with an existing license plate.
     */
    @Test
    void testUpdateCarExistingLicensePlate() {
        car.setLicensePlate("PLACA EXISTE");
        Car car1 = carRepository.save(car);
        Car car2 = carRepository.save(CarHelper.createCar());
        car2.setLicensePlate("PLACA EXISTE");
        carDto = new CarDto(carDto.year(), car2.getLicensePlate(), carDto.model(), carDto.color());

        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<String> response = restTemplate.exchange("/api/cars/" + car1.getId(), HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests updating a car with optional fields.
     */
    @Test
    void testUpdateCarOptionalFields() {
        Car createCar = CarHelper.createCar();
        createCar.setUser(user);
        Car newCar = carRepository.save(createCar);
        carDto = new CarDto(carDto.year(), carDto.licensePlate(), "Novo Modelo", "Nova Cor");

        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<Car> response = restTemplate.exchange("/api/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(carDto.model(), response.getBody().getModel());
        assertEquals(carDto.color(), response.getBody().getColor());
    }

    /**
     * Tests updating a car with partial updates.
     */
    @Test
    void testUpdateCarPartialUpdate() {
        Car createCar = CarHelper.createCar();
        createCar.setUser(user);
        Car newCar = carRepository.save(createCar);
        carDto = new CarDto(2022, carDto.licensePlate(), carDto.model(), carDto.color());
        HttpEntity<CarDto> entity = createJsonEntity(carDto);
        ResponseEntity<Car> response = restTemplate.exchange("/api/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(carDto.year(), response.getBody().getYear());
    }

    /**
     * Tests updating a car with null values.
     */
    @Test
    void testUpdateCarNullValues() {
        Car createCar = CarHelper.createCar();
        createCar.setUser(user);
        Car newCar = carRepository.save(createCar);
        carDto = new CarDto(carDto.year(), carDto.licensePlate(), null, carDto.color());
        HttpEntity<CarDto> entity = createJsonEntity(carDto);
        ResponseEntity<Car> response = restTemplate.exchange("/api/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Creates an HTTP entity with JSON content type.
     *
     * @param carDto the CarDto object to be sent
     * @return an HttpEntity with the CarDto object and JSON headers
     */
    private HttpEntity<CarDto> createJsonEntity(CarDto carDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(carDto, headers);
    }
}
