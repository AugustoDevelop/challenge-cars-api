package com.api.service;

import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.Users;
import com.api.exception.DuplicateResourceException;
import com.api.exception.MissingFieldsException;
import com.api.exception.ResourceNotFoundException;
import com.api.interfaces.UserServiceInterface;
import com.api.repository.CarRepository;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServiceInterface {
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    // para evitar código repetitivo, estou usando essa lib (ModelMapper)
    // para copiar as propriedades do DTO para a entidade
    private final ModelMapper modelMapper;

    public Users createUser(UserDto userDto) {

        if (userDto.getFirstName().isBlank() ||
            userDto.getLastName().isBlank() ||
            userDto.getBirthday().isBlank() ||
            userDto.getLogin().isBlank() ||
            userDto.getPassword().isBlank() ||
            userDto.getEmail().isBlank() ||
            userDto.getPhone().isBlank()
        ) {
            throw new MissingFieldsException(ErrorMessages.MISSING_FIELDS);
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findByLogin(userDto.getLogin()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessages.LOGIN_ALREADY_EXISTS);
        }

        Users users = mapUserDtoToUser(userDto);
        return userRepository.save(users);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS);
        }
        userRepository.deleteById(id);
    }

    public Users updateUser(Long id, UserDto userUpdates) {
        Users users = modelMapper.map(userUpdates, Users.class);
        Users existingUsers = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));

        validateEmailAndLogin(existingUsers, users);

        existingUsers.setFirstName(userUpdates.getFirstName());
        existingUsers.setLastName(userUpdates.getLastName());
        existingUsers.setBirthday(userUpdates.getBirthday());
        existingUsers.setLogin(userUpdates.getLogin());
        existingUsers.setPassword(userUpdates.getPassword());
        existingUsers.setEmail(userUpdates.getEmail());
        existingUsers.setPhone(userUpdates.getPhone());

        if (userUpdates.getCars() != null) {
            updateCars(existingUsers, userUpdates.getCars());
        }

        return userRepository.save(existingUsers);
    }

    private Users mapUserDtoToUser(UserDto userDto) {
        Users users = new Users();
        users.setFirstName(userDto.getFirstName());
        users.setLastName(userDto.getLastName());
        users.setBirthday(userDto.getBirthday());
        users.setLogin(userDto.getLogin());
        users.setPassword(userDto.getPassword());
        users.setEmail(userDto.getEmail());
        users.setPhone(userDto.getPhone());

        if (userDto.getCars() != null) {
            List<Car> cars = userDto.getCars().stream()
                    .map(carDto -> {
                        Car existingCar = carRepository.findByLicensePlate(carDto.getLicensePlate())
                                .orElse(null);

                        if (existingCar != null) {
                            return existingCar;
                        } else {
                            // Se não existir, crie um novo carro
                            Car newCar = new Car();
                            newCar.setYear(carDto.getYear());
                            newCar.setLicensePlate(carDto.getLicensePlate());
                            newCar.setModel(carDto.getModel());
                            newCar.setColor(carDto.getColor());
                            return newCar;
                        }
                    })
                    .toList();

            // Salve os carros que ainda não estão no banco
            List<Car> savedCars = cars.stream()
                    .filter(car -> car.getId() == null) // Filtra carros novos
                    .map(carRepository::save)
                    .toList();

            users.setCars(savedCars);
        }

        return users;
    }

    private void updateCars(Users existingUsers, List<Car> carsUpdates) {
        existingUsers.setCars(new ArrayList<>());
        for (Car carUpdate : carsUpdates) {
            Car existingCar;

            if (carUpdate.getLicensePlate() != null) {
                existingCar = carRepository.findByLicensePlate(carUpdate.getLicensePlate())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
            } else {
                existingCar = new Car();
            }

            existingCar.setYear(carUpdate.getYear());
            existingCar.setLicensePlate(carUpdate.getLicensePlate());
            existingCar.setModel(carUpdate.getModel());
            existingCar.setColor(carUpdate.getColor());

            Car savedCar = carRepository.save(existingCar);

            existingUsers.getCars().add(savedCar);
        }
    }

    private void validateEmailAndLogin(Users existingUsers, Users usersUpdates) {
        if (
                usersUpdates.getEmail() != null &&
                !usersUpdates.getEmail().equals(existingUsers.getEmail()) &&
                userRepository.findByEmail(usersUpdates.getEmail()).isPresent()
        ) {
            throw new DuplicateResourceException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }


        if (
                usersUpdates.getLogin() != null &&
                !usersUpdates.getLogin().equals(existingUsers.getLogin()) &&
                userRepository.findByLogin(usersUpdates.getLogin()).isPresent()
        ) {
            throw new DuplicateResourceException(ErrorMessages.LOGIN_ALREADY_EXISTS);
        }

    }
}
