package com.api.util.mapper;

import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.repository.CarRepository;
import com.api.util.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting UserDto to User entity.
 */
@Component
@AllArgsConstructor
public class UserMapper {
    private final CarRepository carRepository;

    /**
     * Maps a UserDto to a User entity.
     *
     * @param userDto the user data transfer object
     * @return the user entity
     */
    public User mapUserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBirthday(userDto.getBirthday());
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());

        if (userDto.getCars() != null) {
            List<Car> cars = userDto.getCars().stream()
                    .map(carDto -> {
                        Car existingCar = carRepository.findByLicensePlate(carDto.getLicensePlate())
                                .orElse(null);

                        if (existingCar != null) {
                            return existingCar;
                        } else {
                            // If it doesn't exist, create a new car
                            Car newCar = new Car();
                            newCar.setYear(carDto.getYear());
                            newCar.setLicensePlate(carDto.getLicensePlate());
                            newCar.setModel(carDto.getModel());
                            newCar.setColor(carDto.getColor());
                            return newCar;
                        }
                    })
                    .toList();

            // Save the cars that are not yet in the database
            cars.stream()
                .filter(car -> car.getId() == null) // Filter new cars
                .map(carRepository::save)
                .toList();

            user.setCars(cars);
        }
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}