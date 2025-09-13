package com.synchrony.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Exception thrown when validation fails.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidationException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "VALIDATION_ERROR";
    private static final int DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST.value();

    /**
     * Field validation errors.
     */
    private final Map<String, String> fieldErrors;

    /**
     * Constructs a new ValidationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS);
        this.fieldErrors = null;
    }

    /**
     * Constructs a new ValidationException with the specified detail message and field errors.
     * 
     * @param message the detail message
     * @param fieldErrors field validation errors
     */
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, DEFAULT_ERROR_CODE, DEFAULT_HTTP_STATUS, fieldErrors);
        this.fieldErrors = fieldErrors;
    }

    /**
     * Constructs a new ValidationException with the specified detail message and error code.
     * 
     * @param message the detail message
     * @param errorCode the error code
     */
    public ValidationException(String message, String errorCode) {
        super(message, errorCode, DEFAULT_HTTP_STATUS);
        this.fieldErrors = null;
    }

    /**
     * Constructs a new ValidationException with the specified detail message, error code, and field errors.
     * 
     * @param message the detail message
     * @param errorCode the error code
     * @param fieldErrors field validation errors
     */
    public ValidationException(String message, String errorCode, Map<String, String> fieldErrors) {
        super(message, errorCode, DEFAULT_HTTP_STATUS, fieldErrors);
        this.fieldErrors = fieldErrors;
    }

    /**
     * Gets the field validation errors.
     * 
     * @return field validation errors
     */
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Creates a ValidationException for a single field error.
     * 
     * @param fieldName the field name
     * @param errorMessage the error message
     * @return ValidationException instance
     */
    public static ValidationException forField(String fieldName, String errorMessage) {
        return new ValidationException(
                String.format("Validation failed for field '%s': %s", fieldName, errorMessage),
                "FIELD_VALIDATION_ERROR",
                Map.of(fieldName, errorMessage)
        );
    }

    /**
     * Creates a ValidationException for multiple field errors.
     * 
     * @param fieldErrors field validation errors
     * @return ValidationException instance
     */
    public static ValidationException forFields(Map<String, String> fieldErrors) {
        return new ValidationException(
                "Validation failed for multiple fields",
                "MULTIPLE_FIELD_VALIDATION_ERROR",
                fieldErrors
        );
    }

    /**
     * Creates a ValidationException with multiple error messages.
     * 
     * @param message the main error message
     * @param errors list of error messages
     * @return ValidationException instance
     */
    public static ValidationException withErrors(String message, List<String> errors) {
        Map<String, String> fieldErrors = IntStream.range(0, errors.size())
                .boxed()
                .collect(
                    java.util.stream.Collectors.toMap(
                        i -> "error" + i,
                        errors::get
                    )
                );
        return new ValidationException(
                message + ": " + String.join(", ", errors),
                "VALIDATION_ERRORS",
                fieldErrors
        );
    }
}