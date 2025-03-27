package com.api.helpers;

import com.api.entity.User;
import com.api.util.UserStatus;

import java.time.LocalDateTime;

/**
 * Helper class for creating User entities.
 */
public class UsersHelper {

    /**
     * Creates a new User entity with predefined values.
     *
     * @return a User entity
     */
    public static User createUsersEntity() {
        User user = new User();
        user.setFirstName("João");
        user.setLastName("Silva");
        user.setBirthday("1990-01-01");
        user.setLogin("joao.silva");
        user.setPassword("senha123");
        user.setEmail("joao.silva@example.com");
        user.setPhone("1234567890");
        user.setLastLogin(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}