package com.synchrony.common.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiResponse class.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 */
class ApiResponseTest {

    @Test
    void testSuccessWithData() {
        // Given
        String data = "test data";
        String message = "Operation successful";
        
        // When
        ApiResponse<String> response = ApiResponse.success(data, message);
        
        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getCorrelationId());
    }

    @Test
    void testSuccessWithoutData() {
        // Given
        String message = "Operation successful";
        
        // When
        ApiResponse<String> response = ApiResponse.success(message);
        
        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getCorrelationId());
    }

    @Test
    void testSuccessWithCorrelationId() {
        // Given
        String data = "test data";
        String message = "Operation successful";
        String correlationId = "test-correlation-id";
        
        // When
        ApiResponse<String> response = ApiResponse.success(data, message, correlationId);
        
        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(correlationId, response.getCorrelationId());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testError() {
        // Given
        String message = "Operation failed";
        
        // When
        ApiResponse<String> response = ApiResponse.error(message);
        
        // Then
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getCorrelationId());
    }

    @Test
    void testErrorWithCorrelationId() {
        // Given
        String message = "Operation failed";
        String correlationId = "test-correlation-id";
        
        // When
        ApiResponse<String> response = ApiResponse.error(message, correlationId);
        
        // Then
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(correlationId, response.getCorrelationId());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testBuilder() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        String correlationId = "test-correlation-id";
        
        // When
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Test message")
                .data("Test data")
                .timestamp(timestamp)
                .correlationId(correlationId)
                .build();
        
        // Then
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("Test data", response.getData());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(correlationId, response.getCorrelationId());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        ApiResponse<String> response1 = ApiResponse.success("data", "message");
        ApiResponse<String> response2 = ApiResponse.success("data", "message");
        
        // When & Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        ApiResponse<String> response = ApiResponse.success("data", "message");
        
        // When
        String toString = response.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("success=true"));
        assertTrue(toString.contains("message=message"));
        assertTrue(toString.contains("data=data"));
    }
}