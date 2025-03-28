package com.api.helpers;

import com.api.dto.UserResponseDto;

import java.time.LocalDate;
import java.util.Random;

import static java.util.Collections.emptyList;

/**
 * Helper class for creating UserDto objects, facilitating the generation of both valid and invalid instances.
 *
 * <p>This class uses predefined arrays and random number generation to create fake data for valid UserDto objects,
 * making it useful for testing and other scenarios where diverse data is needed.
 */
public class UserResponseDtoHelper {

    private static final String[] FIRST_NAMES = {"Jose", "Vania", "Kauan", "Ana", "Carlos"};
    private static final String[] LAST_NAMES = {"Silva", "Santos", "Oliveira", "Souza", "Rodrigues"};
    private static final String[] EMAIL_DOMAINS = {"@example.com", "@gmail.com", "@hotmail.com"};
    private static final Random random = new Random();

    /**
     * Creates a new UserDto with valid fake values.
     *
     * @return a UserDto object with valid fake values
     */
    public static UserResponseDto createUserResponseDto() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String birthDate = generateRandomBirthDate();
        String username = (firstName + "." + lastName).toLowerCase();
        String email = username + EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
        String phoneNumber = generateRandomPhoneNumber();

        return new UserResponseDto(
                firstName,
                lastName,
                birthDate,
                username,
                email,
                phoneNumber,
                emptyList()
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
