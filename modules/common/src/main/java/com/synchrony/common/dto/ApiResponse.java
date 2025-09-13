package com.synchrony.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for consistent response format across all services.
 * This class provides a unified structure for all API responses in the platform.
 * 
 * @param <T> the type of data being returned
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    /**
     * Indicates if the operation was successful.
     */
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    /**
     * Response message providing additional context.
     */
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    /**
     * Response data payload.
     */
    @Schema(description = "Response data payload")
    private T data;

    /**
     * Timestamp when the response was generated.
     */
    @Schema(description = "Response timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    /**
     * Request correlation ID for tracing.
     */
    @Schema(description = "Request correlation ID for tracing")
    private String correlationId;

    /**
     * Creates a successful response with data.
     * 
     * @param data the response data
     * @param message the success message
     * @param <T> the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response without data.
     * 
     * @param message the success message
     * @param <T> the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with data and correlation ID.
     * 
     * @param data the response data
     * @param message the success message
     * @param correlationId the correlation ID
     * @param <T> the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data, String message, String correlationId) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .build();
    }

    /**
     * Creates an error response.
     * 
     * @param message the error message
     * @param <T> the type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with correlation ID.
     * 
     * @param message the error message
     * @param correlationId the correlation ID
     * @param <T> the type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message, String correlationId) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .build();
    }
}