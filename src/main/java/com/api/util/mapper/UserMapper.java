package com.api.util.mapper;

import com.api.dto.UserDto;
import com.api.entity.Car;
import com.api.entity.User;
import com.api.repository.CarRepository;
import com.api.util.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper responsible for converting UserDto to User entity, handling password encryption and car associations.
 *
 * <p>This class provides a way to transform user data transfer objects into fully formed User entities,
 * including the encryption of passwords and the management of associated cars.
 */
@Component
@AllArgsConstructor
public class UserMapper {
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Maps a UserDto to a User entity, encrypting the password and handling car associations.
     *
     * @param userDto the user data transfer object
     * @return the user entity
     */
    public User mapUserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setBirthday(userDto.birthday());
        user.setLogin(userDto.login());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setEmail(userDto.email());
        user.setPhone(userDto.phone());
        user.setStatus(UserStatus.ACTIVE);

        if (userDto.cars() != null) {
            List<Car> cars = userDto.cars().stream()
                    .map(carDto -> {
                        Car car = carRepository.findByLicensePlate(carDto.licensePlate())
                                .orElseGet(() -> {
                                    Car newCar = new Car();
                                    newCar.setYear(carDto.year());
                                    newCar.setLicensePlate(carDto.licensePlate());
                                    newCar.setModel(carDto.model());
                                    newCar.setColor(carDto.color());
                                    return newCar;
                                });

                        car.setUser(user);
                        return car;
                    })
                    .toList();

            user.setCars(new ArrayList<>(cars));
        }

        return user;
    }
}
