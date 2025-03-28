package com.api.helpers;

import com.api.dto.UserDto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;

/**
 * Helper class for creating UserDto objects, facilitating the generation of both valid and invalid instances.
 *
 * <p>This class uses predefined arrays and random number generation to create fake data for valid UserDto objects,
 * making it useful for testing and other scenarios where diverse data is needed.
 */
public class UserDtoHelper {

    private static final String[] FIRST_NAMES = {"João", "Maria", "Pedro", "Ana", "Carlos"};
    private static final String[] LAST_NAMES = {"Silva", "Santos", "Oliveira", "Souza", "Rodrigues"};
    private static final String[] EMAIL_DOMAINS = {"@example.com", "@gmail.com", "@hotmail.com"};
    private static final Random random = new Random();

    /**
     * Creates a new UserDto with valid fake values.
     *
     * @return a UserDto object with valid fake values
     */
    public static UserDto createUserDto() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String birthDate = generateRandomBirthDate();
        String username = (firstName + "." + lastName).toLowerCase();
        String password = generateRandomPassword();
        String email = username + EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
        String phoneNumber = generateRandomPhoneNumber();

        return new UserDto(
                firstName,
                lastName,
                birthDate,
                username,
                password,
                email,
                phoneNumber,
                Collections.emptyList()
        );
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
     * Generates a random password with a mix of uppercase, lowercase letters, and digits.
     *
     * @return a random password
     */
    private static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int choice = random.nextInt(3);
            switch (choice) {
                case 0:
                    password.append((char) ('a' + random.nextInt(26))); // Lowercase letter
                    break;
                case 1:
                    password.append((char) ('A' + random.nextInt(26))); // Uppercase letter
                    break;
                case 2:
                    password.append(random.nextInt(10)); // Digit
                    break;
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
