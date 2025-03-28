package com.api.dto;

/**
 * Data Transfer Object (DTO) for encapsulating login response details.
 *
 * <p>This DTO is used to return the access token generated upon successful user authentication.
 *
 * @param accessToken The JWT token granted to the user for authenticated access.
 */
public record LoginResponseDTO(
        /**
         * The JWT token granted to the user for authenticated access.
         */
        String accessToken
) {
}
