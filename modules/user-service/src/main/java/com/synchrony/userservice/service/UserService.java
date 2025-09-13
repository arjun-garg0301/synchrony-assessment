package com.synchrony.userservice.service;

import com.synchrony.userservice.dto.UserRegistrationRequest;
import com.synchrony.userservice.dto.UserUpdateRequest;
import com.synchrony.userservice.dto.UserResponse;
import com.synchrony.userservice.entity.User;

/**
 * Service interface for user-related operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * 
     * @param registrationRequest the user registration details
     * @return UserResponse containing the registered user information
     */
    UserResponse registerUser(UserRegistrationRequest registrationRequest);

    /**
     * Creates a new user in the system (alias for registerUser).
     * 
     * @param registrationRequest the user registration details
     * @return UserResponse containing the created user information
     */
    UserResponse createUser(UserRegistrationRequest registrationRequest);

    /**
     * Retrieves user information by username.
     * 
     * @param username the username to search for
     * @return UserResponse containing the user information
     */
    UserResponse getUserByUsername(String username);

    /**
     * Retrieves user information by user ID.
     * 
     * @param userId the user ID to search for
     * @return UserResponse containing the user information
     */
    UserResponse getUserById(Long userId);

    /**
     * Finds a user entity by username.
     * 
     * @param username the username to search for
     * @return User entity
     */
    User findUserByUsername(String username);

    /**
     * Finds a user entity by user ID.
     * 
     * @param userId the user ID to search for
     * @return User entity
     */
    User findUserById(Long userId);

    /**
     * Checks if a user exists by username.
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Updates user information.
     * 
     * @param userId the user ID to update
     * @param updateRequest the update request containing new information
     * @return UserResponse containing the updated user information
     */
    UserResponse updateUser(Long userId, UserUpdateRequest updateRequest);

    /**
     * Deactivates a user account.
     * 
     * @param userId the user ID to deactivate
     */
    void deactivateUser(Long userId);
}