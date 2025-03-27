package com.api.integration;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.helpers.CarDtoHelper;
import com.api.helpers.CarHelper;
import com.api.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the CarController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerIntegrationTest {

    /**
     * TestRestTemplate for making HTTP requests in tests.
     */
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Repository for managing Car entities.
     */
    @Autowired
    private CarRepository carRepository;

    /**
     * Data Transfer Object for Car.
     */
    private CarDto carDto;

    /**
     * Car entity.
     */
    private Car car;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setup() {
        carRepository.deleteAll();
        carDto = CarDtoHelper.createCarDto();
        car = CarHelper.createCar();
    }

    /**
     * Tests the creation of a car successfully.
     */
    @Test
    void testCreateCarSuccess() {
        ResponseEntity<Car> response = restTemplate.postForEntity("/cars/create", carDto, Car.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    /**
     * Tests the creation of a car with missing fields.
     */
    @Test
    void testCreateCarMissingFields() {
        ResponseEntity<String> response = restTemplate.postForEntity("/cars/create", CarDtoHelper.createCarDtoInvalid(), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests the creation of a car with a license plate that already exists.
     */
    @Test
    void testCreateCarLicensePlateAlreadyExists() {
        Car existingCar = carRepository.save(car);
        carDto.setLicensePlate(existingCar.getLicensePlate());

        ResponseEntity<String> response = restTemplate.postForEntity("/cars/create", carDto, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests retrieving all cars successfully.
     */
    @Test
    void testGetAllCarsSuccess() {
        car.setLicensePlate("NEW");
        Car car1 = carRepository.save(car);
        Car car2 = carRepository.save(CarHelper.createCar());
        car2.setModel("Outro Modelo");

        ResponseEntity<List<Car>> response = restTemplate.exchange("/cars", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(c -> c.getId().equals(car1.getId())));
        assertTrue(response.getBody().stream().anyMatch(c -> c.getId().equals(car2.getId())));
    }

    /**
     * Tests retrieving all cars when the list is empty.
     */
    @Test
    void testGetAllCarsEmptyList() {
        carRepository.deleteAll();

        ResponseEntity<List<Car>> response = restTemplate.exchange("/cars", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
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
        Car newCar = carRepository.save(car);

        ResponseEntity<Car> response = restTemplate.getForEntity("/cars/" + newCar.getId(), Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(newCar.getId(), response.getBody().getId());
        assertEquals(newCar.getLicensePlate(), response.getBody().getLicensePlate());
    }

    /**
     * Tests retrieving a car by a non-existing ID.
     */
    @Test
    void testGetCarByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cars/999", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests deleting a car by ID successfully.
     */
    @Test
    void testDeleteCarByIdSuccess() {
        Car newCar = carRepository.save(car);

        ResponseEntity<Void> response = restTemplate.exchange("/cars/" + newCar.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(carRepository.existsById(newCar.getId()));
    }

    /**
     * Tests deleting a car by a non-existing ID.
     */
    @Test
    void testDeleteCarByIdNonExisting() {
        ResponseEntity<String> response = restTemplate.exchange("/cars/999", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Tests updating a car successfully.
     */
    @Test
    void testUpdateCarSuccess() {
        Car newCar = carRepository.save(car);
        carDto.setYear(2022);
        carDto.setLicensePlate(car.getLicensePlate());
        carDto.setModel("Novo Modelo");
        carDto.setColor("Nova Cor");

        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<Car> response = restTemplate.exchange("/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(carDto.getYear(), response.getBody().getYear());
        assertEquals(carDto.getLicensePlate(), response.getBody().getLicensePlate());
        assertEquals(carDto.getModel(), response.getBody().getModel());
        assertEquals(carDto.getColor(), response.getBody().getColor());
    }

    /**
     * Tests updating a car with an invalid license plate.
     */
    @Test
    void testUpdateCarInvalidLicensePlate() {
        Car newCar = carRepository.save(car);
        carDto.setLicensePlate("PLACA INVALIDA");
        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<String> response = restTemplate.exchange("/cars/" + newCar.getId(), HttpMethod.PUT, entity, String.class);

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
        carDto.setLicensePlate(car2.getLicensePlate());

        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<String> response = restTemplate.exchange("/cars/" + car1.getId(), HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * Tests updating a car with optional fields.
     */
    @Test
    void testUpdateCarOptionalFields() {
        Car newCar = carRepository.save(car);
        carDto.setModel("Novo Modelo");
        carDto.setColor("Nova Cor");

        HttpEntity<CarDto> entity = createJsonEntity(carDto);

        ResponseEntity<Car> response = restTemplate.exchange("/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(carDto.getModel(), response.getBody().getModel());
        assertEquals(carDto.getColor(), response.getBody().getColor());
    }

    /**
     * Tests partially updating a car.
     */
    @Test
    void testUpdateCarPartialUpdate() {
        Car newCar = carRepository.save(CarHelper.createCar());
        carDto.setYear(2022);
        HttpEntity<CarDto> entity = createJsonEntity(carDto);
        ResponseEntity<Car> response = restTemplate.exchange("/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(carDto.getYear(), response.getBody().getYear());
    }

    /**
     * Tests updating a car with null values.
     */
    @Test
    void testUpdateCarNullValues() {
        Car newCar = carRepository.save(car);
        carDto.setModel(null);
        HttpEntity<CarDto> entity = createJsonEntity(carDto);
        ResponseEntity<Car> response = restTemplate.exchange("/cars/" + newCar.getId(), HttpMethod.PUT, entity, Car.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Creates an HttpEntity with JSON content type.
     *
     * @param carDto the CarDto to be included in the entity
     * @return the HttpEntity with JSON content type
     */
    private HttpEntity<CarDto> createJsonEntity(CarDto carDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(carDto, headers);
    }
}
