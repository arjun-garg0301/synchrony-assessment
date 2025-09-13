package com.synchrony.userservice.service.impl;

import com.synchrony.common.exception.BusinessException;
import com.synchrony.userservice.dto.LoginRequest;
import com.synchrony.userservice.dto.LoginResponse;
import com.synchrony.userservice.dto.UserResponse;
import com.synchrony.userservice.entity.User;
import com.synchrony.userservice.security.JwtUtil;
import com.synchrony.userservice.service.AuthService;
import com.synchrony.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthService interface.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());
        
        try {
            // Find user by username
            User user = userService.findUserByUsername(loginRequest.getUsername());
            
            // Check if user is active
            if (!user.getIsActive()) {
                log.warn("Authentication failed: User '{}' is not active", loginRequest.getUsername());
                throw new BusinessException("User account is not active", "USER_NOT_ACTIVE");
            }
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Authentication failed: Invalid password for user '{}'", loginRequest.getUsername());
                throw new BusinessException("Invalid username or password", "INVALID_CREDENTIALS");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());
            
            log.info("User '{}' authenticated successfully", loginRequest.getUsername());
            
            // Convert user to UserResponse
            UserResponse userResponse = userService.getUserByUsername(user.getUsername());
            
            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(86400L) // 24 hours in seconds
                    .user(userResponse)
                    .build();
                    
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication for user '{}': {}", loginRequest.getUsername(), e.getMessage(), e);
            throw new BusinessException("Authentication failed", "AUTHENTICATION_ERROR");
        }
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "tokens", key = "#token.hashCode()")
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "tokens", key = "'username:' + #token.hashCode()")
    public String getUsernameFromToken(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }
}