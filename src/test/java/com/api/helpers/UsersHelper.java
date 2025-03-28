package com.api.helpers;

import com.api.entity.User;
import com.api.util.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Helper class for creating User entities, facilitating the generation of valid instances with fake data.
 *
 * <p>This class uses predefined arrays and random number generation to create fake data for valid User entities,
 * making it useful for testing and other scenarios where diverse data is needed.
 */
public class UsersHelper {

    private static final String[] FIRST_NAMES = {"João", "Maria", "Pedro", "Ana", "Carlos"};
    private static final String[] LAST_NAMES = {"Silva", "Santos", "Oliveira", "Souza", "Rodrigues"};
    private static final String[] EMAIL_DOMAINS = {"@example.com", "@gmail.com", "@hotmail.com"};
    private static final Random random = new Random();

    /**
     * Creates a new User entity with valid fake values.
     *
     * @return a User entity
     */
    public static User createUsersEntity() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String birthday = generateRandomBirthDate();
        String login = (firstName + "." + lastName).toLowerCase();
        String password = generateRandomPassword();
        String email = login + EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
        String phone = generateRandomPhoneNumber();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthday(birthday);
        user.setLogin(login);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        user.setCars(CarHelper.createCarList(1, user));
        return user;
    }

    /**
     * Generates a random birth date between 18 and 65 years ago.
     *
     * @return a random birth date
     */
    private static String generateRandomBirthDate() {
        LocalDate today = LocalDate.now();
        long yearsAgo = random.nextInt(47) + 18; // Between 18 and 65 years ago
        LocalDate birthDate = today.minusYears(yearsAgo);
        return birthDate.toString();
    }

    /**
     * Generates a random password with a mix of lowercase letters and numbers.
     *
     * @return a random password
     */
    private static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (random.nextBoolean()) {
                password.append((char) ('a' + random.nextInt(26))); // Lowercase letter
            } else {
                password.append(random.nextInt(10)); // Number
            }
        }
        return password.toString();
    }

    /**
     * Generates a random phone number in the format (XX) XXXXX-XXXX.
     *
     * @return a random phone number
     */
    private static String generateRandomPhoneNumber() {
        String areaCode = String.format("(%02d)", random.nextInt(100));
        String firstPart = String.format("%05d", random.nextInt(100000));
        String secondPart = String.format("%04d", random.nextInt(10000));
        return areaCode + " " + firstPart + "-" + secondPart;
    }
}
