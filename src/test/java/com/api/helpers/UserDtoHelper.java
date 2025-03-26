package com.api.helpers;

import com.api.dto.UserDto;

/**
 * Helper class for creating UserDto objects.
 */
public class UserDtoHelper {

    /**
     * Creates a new UserDto with valid values.
     *
     * @return a UserDto object with valid values
     */
    public static UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("João");
        userDto.setLastName("Silva");
        userDto.setBirthday("1990-01-01");
        userDto.setLogin("joao.silva");
        userDto.setPassword("senha123");
        userDto.setEmail("joao.silva@example.com");
        userDto.setPhone("1234567890");
        return userDto;
    }

    /**
     * Creates a new UserDto with invalid values.
     *
     * @return a UserDto object with invalid values
     */
    public static UserDto createUserDtoInvalid() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("");
        userDto.setLastName("");
        userDto.setBirthday("");
        userDto.setLogin("");
        userDto.setPassword("");
        userDto.setEmail("");
        userDto.setPhone("");
        return userDto;
    }
}