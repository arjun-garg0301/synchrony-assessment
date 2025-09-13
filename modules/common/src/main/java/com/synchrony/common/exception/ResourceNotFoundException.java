package com.synchrony.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceNotFoundException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "RESOURCE_NOT_FOUND";
    private static final int DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND.value();

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and error code.
     * 
     * @param message the detail message
     * @param errorCode the error code
     */
    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode, DEFAULT_HTTP_STATUS);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
    }

    /**
     * Constructs a new ResourceNotFoundException with context information.
     * 
     * @param message the detail message
     * @param errorCode the error code
     * @param context additional context information (e.g., resource ID, type)
     */
    public ResourceNotFoundException(String message, String errorCode, Object context) {
        super(message, errorCode, DEFAULT_HTTP_STATUS, context);
    }

    /**
     * Creates a ResourceNotFoundException for a specific resource type and ID.
     * 
     * @param resourceType the type of resource (e.g., "User", "Image")
     * @param resourceId the ID of the resource
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException forResource(String resourceType, Object resourceId) {
        String message = String.format("%s not found with ID: %s", resourceType, resourceId);
        String errorCode = String.format("%s_NOT_FOUND", resourceType.toUpperCase());
        return new ResourceNotFoundException(message, errorCode, resourceId);
    }

    /**
     * Creates a ResourceNotFoundException for a specific resource type and field.
     * 
     * @param resourceType the type of resource (e.g., "User", "Image")
     * @param fieldName the field name used for lookup
     * @param fieldValue the field value used for lookup
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException forResourceField(String resourceType, String fieldName, Object fieldValue) {
        String message = String.format("%s not found with %s: %s", resourceType, fieldName, fieldValue);
        String errorCode = String.format("%s_NOT_FOUND", resourceType.toUpperCase());
        return new ResourceNotFoundException(message, errorCode, 
                String.format("%s=%s", fieldName, fieldValue));
    }
}