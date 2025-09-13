package com.synchrony.userservice.service;

import com.synchrony.userservice.dto.LoginRequest;
import com.synchrony.userservice.dto.LoginResponse;

/**
 * Service interface for authentication operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * Authenticates a user with username and password.
     * 
     * @param loginRequest the login request containing username and password
     * @return LoginResponse containing JWT token and user information
     */
    LoginResponse authenticate(LoginRequest loginRequest);

    /**
     * Validates a JWT token.
     * 
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts username from JWT token.
     * 
     * @param token the JWT token
     * @return username if token is valid, null otherwise
     */
    String getUsernameFromToken(String token);
}