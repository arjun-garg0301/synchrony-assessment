package com.synchrony.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard error response DTO for API error responses across all services.
 * Provides comprehensive error information including validation errors and stack traces.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    @Schema(description = "Error timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * Error type or category.
     */
    @Schema(description = "Error type", example = "VALIDATION_ERROR")
    private String error;

    /**
     * Detailed error message.
     */
    @Schema(description = "Error message", example = "Invalid input parameters")
    private String message;

    /**
     * Request path where the error occurred.
     */
    @Schema(description = "Request path", example = "/api/v1/users")
    private String path;

    /**
     * HTTP method used in the request.
     */
    @Schema(description = "HTTP method", example = "POST")
    private String method;

    /**
     * Request correlation ID for tracing.
     */
    @Schema(description = "Correlation ID for tracing")
    private String correlationId;

    /**
     * Validation errors (field-level errors).
     */
    @Schema(description = "Field validation errors")
    private Map<String, String> validationErrors;

    /**
     * List of error details for complex scenarios.
     */
    @Schema(description = "Detailed error information")
    private List<ErrorDetail> details;

    /**
     * Stack trace (only in development mode).
     */
    @Schema(description = "Stack trace (development only)")
    private String stackTrace;

    /**
     * Inner class for detailed error information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Detailed error information")
    public static class ErrorDetail {
        
        /**
         * Error code for programmatic handling.
         */
        @Schema(description = "Error code", example = "FIELD_REQUIRED")
        private String code;
        
        /**
         * Field name (for validation errors).
         */
        @Schema(description = "Field name", example = "username")
        private String field;
        
        /**
         * Error message for this specific detail.
         */
        @Schema(description = "Error message", example = "Username is required")
        private String message;
        
        /**
         * Rejected value (for validation errors).
         */
        @Schema(description = "Rejected value")
        private Object rejectedValue;
    }

    /**
     * Creates a basic error response.
     * 
     * @param status HTTP status code
     * @param error error type
     * @param message error message
     * @param path request path
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Creates an error response with correlation ID.
     * 
     * @param status HTTP status code
     * @param error error type
     * @param message error message
     * @param path request path
     * @param correlationId correlation ID
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(int status, String error, String message, String path, String correlationId) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .correlationId(correlationId)
                .build();
    }

    /**
     * Creates an error response with validation errors.
     * 
     * @param status HTTP status code
     * @param error error type
     * @param message error message
     * @param path request path
     * @param validationErrors field validation errors
     * @return ErrorResponse instance
     */
    public static ErrorResponse withValidationErrors(int status, String error, String message, 
                                                   String path, Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}