package com.api.service;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.exception.ResourceNotFoundException;
import com.api.helpers.CarDtoHelper;
import com.api.helpers.CarHelper;
import com.api.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private CarDto carDto;

    @BeforeEach
    void setUp() {
        car = CarHelper.createCar();
        carDto = CarDtoHelper.createCarDto();
    }

    @Test
    void testCreateCarSuccess() {
        when(modelMapper.map(any(CarDto.class), eq(Car.class))).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car createdCar = carService.createCar(carDto);

        assertNotNull(createdCar);
        assertEquals(car, createdCar);
        verify(carRepository, times(1)).save(any(Car.class));
    }


    @Test
    void testGetAllCars() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carRepository.findAll()).thenReturn(cars);

        List<Car> retrievedCars = carService.getAllCars();

        assertNotNull(retrievedCars);
        assertEquals(cars, retrievedCars);
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void testGetCarByIdSuccess() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        Car retrievedCar = carService.getCarById(1L);

        assertNotNull(retrievedCar);
        assertEquals(car, retrievedCar);
        verify(carRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCarByIdNonExisting() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> carService.getCarById(1L));

        verify(carRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateCarSuccess() {
        Car existingCar = new Car();
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);

        carDto.setYear(2020);
        carDto.setLicensePlate("ABC1234");
        carDto.setModel("Modelo");
        carDto.setColor("Cor");

        Car updatedCar = carService.updateCar(1L, carDto);

        assertNotNull(updatedCar);
        assertEquals(existingCar, updatedCar);

        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testUpdateCarNonExisting() {
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.updateCar(1L, carDto));
        verify(carRepository, times(1)).findByLicensePlate(anyString());
    }


    @Test
    void testDeleteCarSuccess() {
        when(carRepository.existsById(anyLong())).thenReturn(true);
        carService.deleteCar(1L);

        verify(carRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteCarNonExisting() {
        when(carRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> carService.deleteCar(1L));

        verify(carRepository, times(1)).existsById(anyLong());
    }
}
