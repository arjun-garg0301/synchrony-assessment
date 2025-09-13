package com.synchrony.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user update request.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

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
     */
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
}