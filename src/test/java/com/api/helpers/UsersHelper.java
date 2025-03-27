package com.api.helpers;

import com.api.entity.Users;

public class UsersHelper {

    public static Users createUsersEntity() {
        Users users = new Users();
        users.setFirstName("João");
        users.setLastName("Silva");
        users.setBirthday("1990-01-01");
        users.setLogin("joao.silva");
        users.setPassword("senha123");
        users.setEmail("joao.silva@example.com");
        users.setPhone("1234567890");
        users.setCreatedAt("2023-01-01 12:00:00");
        users.setLastLogin("2023-01-01 12:00:00");
        return users;
    }
}
