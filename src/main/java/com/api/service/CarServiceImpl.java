package com.api.service;

import com.api.dto.CarDto;
import com.api.entity.Car;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.CarServiceInterface;
import com.api.repository.CarRepository;
import com.api.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarServiceInterface {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    public Car createCar(CarDto carDto) {
        if (carDto.getLicensePlate().isBlank() || carDto.getModel().isBlank() || carDto.getColor().isBlank()) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
        if (carRepository.findByLicensePlate(carDto.getLicensePlate()).isPresent()) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
        Car car = modelMapper.map(carDto, Car.class);
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
    }

    public Car updateCar(Long id, CarDto carDto) {
        validateCars(carDto);
        Optional<Car> existingCar = getCarByLicensePlate(carDto.getLicensePlate());
        if (existingCar.isPresent()) {
            existingCar.get().setYear(carDto.getYear());
            existingCar.get().setLicensePlate(carDto.getLicensePlate());
            existingCar.get().setModel(carDto.getModel());
            existingCar.get().setColor(carDto.getColor());
            return carRepository.save(existingCar.get());
        } else {
            throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
        }
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
        }
        carRepository.deleteById(id);
    }

    private void validateCars(CarDto carDto) {
        if (carDto.getLicensePlate().isBlank() ||
            carDto.getModel().isBlank() ||
            carDto.getColor().isBlank() ||
            carDto.getYear() == null
        ) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }
    }

    private Optional<Car> getCarByLicensePlate(String licensePlate) {
        return Optional.ofNullable(carRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS)));
    }

}

