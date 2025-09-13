package com.synchrony.userservice.service;

import com.synchrony.common.exception.BusinessException;
import com.synchrony.common.exception.ResourceNotFoundException;
import com.synchrony.common.exception.ValidationException;
import com.synchrony.common.util.ValidationUtil;
import com.synchrony.userservice.dto.UserRegistrationRequest;
import com.synchrony.userservice.dto.UserResponse;
import com.synchrony.userservice.dto.UserUpdateRequest;
import com.synchrony.userservice.entity.User;
import com.synchrony.userservice.repository.UserRepository;
import com.synchrony.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Enhanced with comprehensive test coverage and proper exception testing.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequest registrationRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registrationRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("SecurePassword123!")  // 8+ characters
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1-234-567-8900")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1-234-567-8900")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void registerUser_Success() {
        // Arrange - Mock successful validations and repository operations
        when(validationUtil.isValidUsername("testuser")).thenReturn(true);
        when(validationUtil.isValidEmail("test@example.com")).thenReturn(true);
        when(validationUtil.isValidPassword("SecurePassword123!")).thenReturn(true);
        when(validationUtil.isValidPhoneNumber("+1-234-567-8900")).thenReturn(true);
        when(validationUtil.isValidLength("Test", 1, 50)).thenReturn(true);
        when(validationUtil.isValidLength("User", 1, 50)).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse result = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertTrue(result.getIsActive());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("SecurePassword123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsBusinessException() {
        // Arrange - Mock valid request but duplicate username
        when(validationUtil.isValidUsername("testuser")).thenReturn(true);
        when(validationUtil.isValidEmail("test@example.com")).thenReturn(true);
        when(validationUtil.isValidPassword("SecurePassword123!")).thenReturn(true);
        when(validationUtil.isValidPhoneNumber("+1-234-567-8900")).thenReturn(true);
        when(validationUtil.isValidLength("Test", 1, 50)).thenReturn(true);
        when(validationUtil.isValidLength("User", 1, 50)).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerUser(registrationRequest);
        });

        assertEquals("USER_ALREADY_EXISTS", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsBusinessException() {
        // Arrange - Mock valid request but duplicate email
        when(validationUtil.isValidUsername("testuser")).thenReturn(true);
        when(validationUtil.isValidEmail("test@example.com")).thenReturn(true);
        when(validationUtil.isValidPassword("SecurePassword123!")).thenReturn(true);
        when(validationUtil.isValidPhoneNumber("+1-234-567-8900")).thenReturn(true);
        when(validationUtil.isValidLength("Test", 1, 50)).thenReturn(true);
        when(validationUtil.isValidLength("User", 1, 50)).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerUser(registrationRequest);
        });

        assertEquals("EMAIL_ALREADY_EXISTS", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidUsername_ThrowsValidationException() {
        // Arrange - Create request with invalid username (less than 3 characters)
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .username("ab") // Too short
                .email("test@example.com")
                .password("ValidPassword123!")
                .build();

        // Mock validation to return false for invalid username
        when(validationUtil.isValidUsername("ab")).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(invalidRequest);
        });

        assertTrue(exception.getMessage().contains("Username must be 3-20 characters long"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidEmail_ThrowsValidationException() {
        // Arrange - Create request with invalid email (no @ symbol)
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .email("invalid-email") // No @ symbol
                .password("ValidPassword123!")
                .build();

        // Mock validation - valid username but invalid email
        when(validationUtil.isValidUsername("testuser")).thenReturn(true);
        when(validationUtil.isValidEmail("invalid-email")).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(invalidRequest);
        });

        assertTrue(exception.getMessage().contains("Email must be a valid email address format"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidPassword_ThrowsValidationException() {
        // Arrange - Create request with invalid password (less than 6 characters)
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("1234567") // Too short (7 chars, need 8)
                .build();

        // Mock validation - valid username and email but invalid password
        when(validationUtil.isValidUsername("testuser")).thenReturn(true);
        when(validationUtil.isValidEmail("test@example.com")).thenReturn(true);
        when(validationUtil.isValidPassword("1234567")).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(invalidRequest);
        });

        assertTrue(exception.getMessage().contains("Password must be at least 8 characters long"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findUserByUsername_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserByUsername("nonexistent");
        });

        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("User not found with username"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void findUserById_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserById_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(999L);
        });

        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("User not found with ID"));
        verify(userRepository).findById(999L);
    }

    @Test
    void getUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Act
        UserResponse result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByEmail_ReturnsFalse() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void updateUser_Success() {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .phoneNumber("+9876543210")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(validationUtil.isNotNullOrEmpty(anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse result = userService.updateUser(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deactivateUser_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.deactivateUser(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
}