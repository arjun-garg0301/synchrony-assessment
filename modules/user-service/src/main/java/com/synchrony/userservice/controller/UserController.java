package com.synchrony.userservice.controller;

import com.synchrony.common.dto.ApiResponse;
import com.synchrony.userservice.dto.UserRegistrationRequest;
import com.synchrony.userservice.dto.UserUpdateRequest;
import com.synchrony.userservice.dto.UserResponse;
import com.synchrony.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves user by username.
     * 
     * @param username the username to search for
     * @return ResponseEntity containing the user response
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve user information by username")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        log.debug("Retrieving user by username: {}", username);
        
        UserResponse userResponse = userService.getUserByUsername(username);
        
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User retrieved successfully"));
    }

    /**
     * Retrieves user by ID.
     * 
     * @param userId the user ID to search for
     * @return ResponseEntity containing the user response
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        log.debug("Retrieving user by ID: {}", userId);
        
        UserResponse userResponse = userService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User retrieved successfully"));
    }

    /**
     * Updates user information.
     * 
     * @param userId the user ID to update
     * @param updateRequest the user update request
     * @return ResponseEntity containing the updated user response
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        log.info("Updating user with ID: {}", userId);
        
        UserResponse userResponse = userService.updateUser(userId, updateRequest);
        
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User updated successfully"));
    }

    /**
     * Deactivates a user.
     * 
     * @param userId the user ID to deactivate
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long userId) {
        log.info("Deactivating user with ID: {}", userId);
        
        userService.deactivateUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
    }

    /**
     * Checks if username exists.
     * 
     * @param username the username to check
     * @return ResponseEntity indicating if username exists
     */
    @GetMapping("/exists/username/{username}")
    @Operation(summary = "Check username availability", description = "Check if username is already taken")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(@PathVariable String username) {
        log.debug("Checking if username exists: {}", username);
        
        boolean exists = userService.existsByUsername(username);
        
        return ResponseEntity.ok(ApiResponse.success(exists, "Username availability checked"));
    }

    /**
     * Checks if email exists.
     * 
     * @param email the email to check
     * @return ResponseEntity indicating if email exists
     */
    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Check email availability", description = "Check if email is already registered")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@PathVariable String email) {
        log.debug("Checking if email exists: {}", email);
        
        boolean exists = userService.existsByEmail(email);
        
        return ResponseEntity.ok(ApiResponse.success(exists, "Email availability checked"));
    }
}