package com.synchrony.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login request.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Username for authentication.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Password for authentication.
     */
    @NotBlank(message = "Password is required")
    private String password;
}