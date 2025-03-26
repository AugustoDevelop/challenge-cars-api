package com.api.controller;

import com.api.dto.UserDto;
import com.api.entity.Users;
import com.api.interfaces.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceInterface userService;

    @PostMapping("/create")
    public ResponseEntity<Users> createUser(@RequestBody @Valid UserDto userDto) {
        Users createdUsers = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        Users users = userService.getUserById(id);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        Users updatedUsers = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUsers);
    }
}
