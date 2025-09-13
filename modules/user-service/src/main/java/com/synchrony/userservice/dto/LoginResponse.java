package com.synchrony.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response containing JWT token and user information.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT access token.
     */
    private String accessToken;

    /**
     * Token type (typically "Bearer").
     */
    private String tokenType;

    /**
     * Token expiration time in seconds.
     */
    private Long expiresIn;

    /**
     * User information.
     */
    private UserResponse user;
}