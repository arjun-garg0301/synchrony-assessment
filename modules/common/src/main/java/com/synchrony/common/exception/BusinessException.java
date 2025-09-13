package com.synchrony.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for business logic violations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class BusinessException extends BaseException {

    /**
     * Constructs a new BusinessException with the specified detail message.
     * 
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Constructs a new BusinessException with the specified detail message and error code.
     * 
     * @param message the detail message
     * @param errorCode the error code
     */
    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Constructs a new BusinessException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Constructs a new BusinessException with the specified detail message, error code, and cause.
     * 
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause, errorCode, HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Constructs a new BusinessException with the specified detail message, error code, and HTTP status.
     * 
     * @param message the detail message
     * @param errorCode the error code
     * @param httpStatus the HTTP status code
     */
    public BusinessException(String message, String errorCode, int httpStatus) {
        super(message, errorCode, httpStatus);
    }
}