package com.api.helpers;

import com.api.dto.UserDto;

public class UserDtoHelper {

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
