package com.synchrony.userservice.controller;

import com.synchrony.common.dto.ApiResponse;
import com.synchrony.userservice.dto.LoginRequest;
import com.synchrony.userservice.dto.LoginResponse;
import com.synchrony.userservice.dto.UserRegistrationRequest;
import com.synchrony.userservice.dto.UserResponse;
import com.synchrony.userservice.service.AuthService;
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
 * REST controller for authentication operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Authenticates a user and returns JWT token.
     * 
     * @param loginRequest the login request
     * @return ResponseEntity containing login response with JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        LoginResponse loginResponse = authService.authenticate(loginRequest);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
    }

    /**
     * Registers a new user.
     * 
     * @param registrationRequest the user registration request
     * @return ResponseEntity containing the created user response
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        log.info("Registration attempt for user: {}", registrationRequest.getUsername());
        
        UserResponse userResponse = userService.registerUser(registrationRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "User registered successfully"));
    }

    /**
     * Validates a JWT token.
     * 
     * @param token the JWT token to validate
     * @return ResponseEntity indicating if token is valid
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        log.debug("Token validation request");
        
        boolean isValid = authService.validateToken(token);
        
        return ResponseEntity.ok(ApiResponse.success(isValid, "Token validation completed"));
    }
}