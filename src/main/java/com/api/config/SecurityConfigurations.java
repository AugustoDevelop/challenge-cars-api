package com.api.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central configuration class for application security settings.
 * Configures web security, JWT authentication, and authorization rules.
 *
 * <p>This configuration:
 * <ul>
 *   <li>Disables CSRF protection for stateless API</li>
 *   <li>Sets session management to stateless</li>
 *   <li>Defines public endpoints for authentication and API documentation</li>
 *   <li>Registers JWT security filter</li>
 *   <li>Configures password encoding strategy</li>
 * </ul>
 */
@Profile(value = {"production", "test", "local"})
@Configuration
@EnableWebSecurity
@SecurityScheme(
        name = SecurityConfigurations.SECURITY,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SecurityConfigurations {

    /**
     * The security filter used for JWT authentication.
     */
    @Autowired
    SecurityFilter securityFilter;

    /**
     * The name of the security scheme used in Swagger/OpenAPI documentation.
     */
    public static final String SECURITY = "bearerAuth";
    /**
     * List of Swagger/OpenAPI endpoints accessible without authentication
     */
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "swagger-ui/**",
            "swagger-ui.html",
            "/swagger-resources/**",
    };

    /**
     * Configures the main security filter chain for the application.
     *
     * @param httpSecurity the base security configuration builder
     * @return the configured security filter chain
     * @throws Exception if configuration error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/cars/**").authenticated()
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/singin").permitAll()
                                .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Exposes the authentication manager bean for use in other components.
     *
     * @param authenticationConfiguration the authentication configuration
     * @return the configured AuthenticationManager
     * @throws Exception if configuration error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the password encoder strategy for the application.
     *
     * @return BCrypt password encoder implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}