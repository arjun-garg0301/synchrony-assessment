package com.synchrony.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.common.exception.GlobalExceptionHandler;
import com.synchrony.common.exception.ResourceNotFoundException;
import com.synchrony.userservice.dto.UserResponse;
import com.synchrony.userservice.dto.UserUpdateRequest;
import com.synchrony.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 * 
 * Simplified approach using Mockito extensions instead of Spring Boot test slices
 * to avoid ApplicationContext loading issues while maintaining test coverage.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        
        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("+1-234-567-8900")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetUserById() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void testGetUserByUsername() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("+1-987-654-3210")
                .build();

        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("+1-987-654-3210")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Updated"));

        verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    void testDeactivateUser() throws Exception {
        // Arrange
        doNothing().when(userService).deactivateUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deactivated successfully"));

        verify(userService).deactivateUser(1L);
    }

    @Test
    void testCheckUsernameExists() throws Exception {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/v1/users/exists/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existsByUsername("testuser");
    }

    @Test
    void testCheckEmailExists() throws Exception {
        // Arrange
        when(userService.existsByEmail("test@example.com")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/v1/users/exists/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));

        verify(userService).existsByEmail("test@example.com");
    }

    @Test
    void testGetUserNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenThrow(ResourceNotFoundException.forResource("User", 999L));

        // Act & Assert
        mockMvc.perform(get("/v1/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstName("Updated")
                .build();

        when(userService.updateUser(eq(999L), any(UserUpdateRequest.class)))
                .thenThrow(ResourceNotFoundException.forResource("User", 999L));

        // Act & Assert
        mockMvc.perform(put("/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any(UserUpdateRequest.class));
    }

    @Test
    void testInvalidUpdateRequest() throws Exception {
        // Arrange - Empty request body should be handled gracefully
        UserUpdateRequest invalidRequest = UserUpdateRequest.builder().build();

        // Act & Assert
        mockMvc.perform(put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // Should be OK as empty updates are allowed

        verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }
}