package com.api.config;

import com.api.dto.CarDto;
import com.api.dto.UserResponseDto;
import com.api.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper bean.
 *
 * <p>This class sets up a custom ModelMapper bean with specific type mappings and converters.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates a ModelMapper bean with custom type mappings.
     *
     * @return a configured ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom converter for User to UserResponseDto
        TypeMap<User, UserResponseDto> typeMap = modelMapper.createTypeMap(User.class, UserResponseDto.class);
        typeMap.setConverter(new AbstractConverter<>() {
            @Override
            protected UserResponseDto convert(User user) {
                return new UserResponseDto(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthday(),
                        user.getLogin(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getCars().stream()
                                .map(car -> new CarDto(
                                        car.getYear(),
                                        car.getLicensePlate(),
                                        car.getModel(),
                                        car.getColor()
                                ))
                                .toList()
                );
            }
        });

        return modelMapper;
    }
}