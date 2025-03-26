package com.api.interfaces;

import com.api.dto.UserDto;
import com.api.entity.Users;

import java.util.List;

public interface UserServiceInterface {
    Users createUser(UserDto userDto);
    List<Users> getAllUsers();
    Users getUserById(Long id);
    void deleteUserById(Long id);
    Users updateUser(Long id, UserDto userUpdates);
}
