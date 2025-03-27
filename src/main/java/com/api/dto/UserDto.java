package com.api.dto;

import com.api.entity.Car;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data transfer object for user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Birthday is required")
    private String birthday;

    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private List<Car> cars;

    /**
     * Constructs a new UserDto with the specified details.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param birthday  the birthday of the user
     * @param login     the login of the user
     * @param password  the password of the user
     * @param email     the email of the user
     * @param phone     the phone number of the user
     */
    public UserDto(String firstName, String lastName, String birthday, String login, String password, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.login = login;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }
}