package com.api.config;

import com.api.entity.User;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.UserRepository;
import com.api.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Custom implementation of {@link UserDetailsService} to load user-specific data.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    /**
     * Loads the user by their username (email).
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never {@code null})
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.repository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_FIELDS));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}