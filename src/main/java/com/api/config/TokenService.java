package com.api.config;

import com.api.entity.User;
import com.api.exception.InvalidFieldsException;
import com.api.exception.UnauthorizedException;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Service for generating and validating JWT tokens for user authentication.
 *
 * <p>This service handles the creation of JWT tokens using a secret key and provides methods to validate
 * the tokens and retrieve the logged-in user.
 */
@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private final UserRepository userRepository;

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user for whom to generate the token
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("challenger-cars-api")
                    .withSubject(user.getLogin())
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);
            
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while authenticating");
        }
    }

    /**
     * Validates the provided JWT token and returns the subject (username) if valid.
     *
     * @param token the JWT token to validate
     * @return the username if the token is valid, null otherwise
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("challenger-cars-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Retrieves the currently logged-in user from the security context.
     *
     * @return the logged-in user
     * @throws UnauthorizedException if the user is not authenticated
     */
    public User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByLogin(username).orElseThrow(() -> new InvalidFieldsException(ErrorMessages.INVALID_FIELDS));
        } else {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED);
        }
    }

    /**
     * Generates an expiration date for the JWT token, set to 2 hours from now.
     *
     * @return the expiration date as an Instant
     */
    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}