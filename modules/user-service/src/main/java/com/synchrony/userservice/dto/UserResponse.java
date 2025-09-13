package com.synchrony.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * User ID.
     */
    private Long id;

    /**
     * Username.
     */
    private String username;

    /**
     * Email address.
     */
    private String email;

    /**
     * First name.
     */
    private String firstName;

    /**
     * Last name.
     */
    private String lastName;

    /**
     * Phone number.
     */
    private String phoneNumber;

    /**
     * Whether the user account is active.
     */
    private Boolean isActive;

    /**
     * User creation timestamp.
     */
    private LocalDateTime createdAt;

    /**
     * User last update timestamp.
     */
    private LocalDateTime updatedAt;

    /**
     * List of user's images.
     */
    private java.util.List<ImageResponse> images;
}