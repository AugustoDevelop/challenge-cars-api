package com.api.service;

import com.api.config.TokenService;
import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.exception.ResourceNotFoundException;
import com.api.helpers.CarDtoHelper;
import com.api.helpers.CarHelper;
import com.api.helpers.UsersHelper;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the CarServiceImpl, covering various scenarios such as creation, retrieval, update, and deletion of cars.
 *
 * <p>This test class ensures that the CarServiceImpl behaves as expected under different conditions, including valid and invalid inputs.
 */
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@MockitoSettings(strictness = Strictness.LENIENT)
class CarServiceImplTest {

    /**
     * Mocked CarRepository for testing purposes.
     */
    @Mock
    private CarRepository carRepository;

    /**
     * Mocked ModelMapper for testing purposes.
     */
    @Mock
    private ModelMapper modelMapper;

    /**
     * Injected instance of CarServiceImpl for testing.
     */

    private CarServiceImpl carService;

    /**
     * Mocked TokenService for testing purposes.
     */
    @Mock
    private TokenService tokenService;

    /**
     * Mocked UserRepository for testing purposes.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Car entity used in tests.
     */
    private Car car;

    /**
     * Data Transfer Object for Car used in tests.
     */
    private CarDto carDto;

    private User user;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        carRepository = mock(CarRepository.class);
        modelMapper = mock(ModelMapper.class);
        tokenService = mock(TokenService.class);
        carService = new CarServiceImpl(carRepository, tokenService);

        car = CarHelper.createCar();
        carDto = CarDtoHelper.createCarDto();
        user = UsersHelper.createUsersEntity();
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(car));
        when(tokenService.getLoggedUser()).thenReturn(user);
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));
        // Mock the SecurityContext and set the authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    /**
     * Tests the successful creation of a car.
     */
    @Test
    void testCreateCarSuccess() {
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(modelMapper.map(any(CarDto.class), eq(Car.class))).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        Car createdCar = carService.createCar(carDto);

        assertNotNull(createdCar);
        assertEquals(car, createdCar);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    /**
     * Tests retrieving all cars.
     */
    @Test
    void testGetAllCars() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carRepository.findByUser_Login(anyString())).thenReturn(cars);

        List<Car> retrievedCars = carService.getAllCars();

        assertNotNull(retrievedCars);
        assertEquals(cars, retrievedCars);
        verify(carRepository, times(1)).findByUser_Login(anyString());
    }

    /**
     * Tests retrieving a car by ID successfully.
     */
    @Test
    void testGetCarByIdSuccess() {
        when(carRepository.findByIdAndUser_Login(anyLong(), anyString())).thenReturn(Optional.of(car));
        Car retrievedCar = carService.getCarById(1L);

        assertNotNull(retrievedCar);
        assertEquals(car, retrievedCar);
        verify(carRepository, times(1)).findByIdAndUser_Login(anyLong(), anyString());
    }

    /**
     * Tests retrieving a car by a non-existing ID.
     */
    @Test
    void testGetCarByIdNonExisting() {
        when(carRepository.findByIdAndUser_Login(anyLong(), anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> carService.getCarById(1L));

        verify(carRepository, times(1)).findByIdAndUser_Login(anyLong(), anyString());
    }

    /**
     * Tests the successful update of a car.
     */
    @Test
    void testUpdateCarSuccess() {
        Car existingCar = new Car();
        when(carRepository.findByIdAndUser_Login(anyLong(), anyString())).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);

        carDto = new CarDto(2020, "ABC1234", "Modelo", "Cor");

        Car updatedCar = carService.updateCar(1L, carDto);

        assertNotNull(updatedCar);
        assertEquals(existingCar, updatedCar);

        verify(carRepository, times(1)).save(any(Car.class));
    }

    /**
     * Tests updating a car with a non-existing license plate.
     */
    @Test
    void testUpdateCarNonExisting() {
        when(carRepository.findByIdAndUser_Login(anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.updateCar(1L, carDto));
        verify(carRepository, times(1)).findByIdAndUser_Login(anyLong(), anyString());
    }

    /**
     * Tests the successful deletion of a car by ID.
     */
    @Test
    void testDeleteCarSuccess() {
        when(carRepository.findByIdAndUser_Login(anyLong(), anyString())).thenReturn(Optional.of(car));
        when(carRepository.existsById(anyLong())).thenReturn(true);
        carService.deleteCar(1L);

        verify(carRepository, times(1)).delete(any());
    }

    /**
     * Tests deleting a car by a non-existing ID.
     */
    @Test
    void testDeleteCarNonExisting() {
        when(carRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> carService.deleteCar(1L));

        verify(carRepository, never()).delete(any());
    }
}
