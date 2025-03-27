package com.api.util.mapper;

import com.api.dto.CarDto;
import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.helpers.CarDtoHelper;
import com.api.helpers.CarHelper;
import com.api.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserMapper class.
 */
class UserMapperTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private UserMapper userMapper;

    private Car car;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        car = CarHelper.createCar();
    }

    /**
     * Tests mapping a UserDto to a User entity when the car already exists in the repository.
     */
    @Test
    void testMapUserDtoToUser_WithExistingCar() {
        // Arrange
        car.setId(1L);
        car.setLicensePlate("ABC1234");

        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(car));

        CarDto carDto = CarDtoHelper.createCarDto();
        carDto.setLicensePlate("ABC1234");

        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setCars(List.of(car));

        // Act
        User user = userMapper.mapUserDtoToUser(userDto);

        // Assert
        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(1, user.getCars().size());
        assertEquals(car, user.getCars().get(0));

        verify(carRepository, times(1)).findByLicensePlate("ABC1234");
    }

    /**
     * Tests mapping a UserDto to a User entity when the car does not exist in the repository.
     */
    @Test
    void testMapUserDtoToUser_WithNewCar() {
        // Arrange
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        Car car = CarHelper.createCar();

        UserDto userDto = new UserDto();
        userDto.setFirstName("Jane");
        userDto.setLastName("Smith");
        userDto.setCars(List.of(car));

        // Act
        User user = userMapper.mapUserDtoToUser(userDto);

        // Assert
        assertNotNull(user);
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(1, user.getCars().size());
        Car newCar = user.getCars().get(0);
        assertEquals(car.getLicensePlate(), newCar.getLicensePlate());
        assertEquals(car.getYear(), newCar.getYear());
        assertEquals(car.getModel(), newCar.getModel());
        assertEquals(car.getColor(), newCar.getColor());

        verify(carRepository, times(1)).findByLicensePlate(car.getLicensePlate());
        verify(carRepository, times(1)).save(any(Car.class));
    }
}