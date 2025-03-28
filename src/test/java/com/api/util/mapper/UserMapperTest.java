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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserMapper class, ensuring correct mapping from UserDto to User entity.
 *
 * <p>This test class verifies the behavior of the UserMapper, particularly in scenarios where cars
 * may either exist in the repository or need to be created as new instances.
 */
@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    private Car car;
    private CarDto carDto;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userMapper = new UserMapper(carRepository, passwordEncoder);

        car = CarHelper.createCar();
        carDto = CarDtoHelper.createCarDto();
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

        CarDto carDto = new CarDto(car.getYear(), car.getLicensePlate(), car.getModel(), car.getColor());

        UserDto userDto = new UserDto("John", "Doe", "1990-01-01", "john.doe", "password", "john.doe@example.com", "123456789", List.of(carDto));

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

        UserDto userDto = new UserDto("Jane", "Smith", "1990-01-01", "jane.smith", "password", "jane.smith@example.com", "987654321", List.of(carDto));

        // Act
        User user = userMapper.mapUserDtoToUser(userDto);

        // Assert
        assertNotNull(user);
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(1, user.getCars().size());

        Car newCar = user.getCars().get(0);
        assertEquals(carDto.licensePlate(), newCar.getLicensePlate());
        assertEquals(carDto.year(), newCar.getYear());
        assertEquals(carDto.model(), newCar.getModel());
        assertEquals(carDto.color(), newCar.getColor());

        verify(carRepository, times(1)).findByLicensePlate(carDto.licensePlate());
    }
}
