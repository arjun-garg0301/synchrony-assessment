package com.synchrony.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    /**
     * Username for the new user.
     * Must be 3-20 characters long and contain only alphanumeric characters and underscores.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Username must contain only alphanumeric characters and underscores")
    private String username;

    /**
     * Email address for the new user.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Password for the new user.
     * Must be at least 8 characters long for enterprise security standards.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    /**
     * First name of the user.
     */
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    /**
     * Last name of the user.
     */
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    /**
     * Phone number of the user.
     * Optional field with international format validation.
     */
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,20}$", message = "Phone number must be a valid international format")
    private String phoneNumber;
}