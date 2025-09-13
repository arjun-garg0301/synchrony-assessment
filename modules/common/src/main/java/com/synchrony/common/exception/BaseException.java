package com.synchrony.common.exception;

import lombok.Getter;

/**
 * Base exception class for all custom exceptions in the platform.
 * Provides common functionality and error codes for consistent error handling.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * Error code for programmatic handling.
     */
    private final String errorCode;

    /**
     * HTTP status code associated with this exception.
     */
    private final int httpStatus;

    /**
     * Additional context information.
     */
    private final Object context;

    /**
     * Constructs a new BaseException with the specified detail message.
     * 
     * @param message the detail message
     * @param errorCode the error code
     * @param httpStatus the HTTP status code
     */
    protected BaseException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = null;
    }

    /**
     * Constructs a new BaseException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     * @param errorCode the error code
     * @param httpStatus the HTTP status code
     */
    protected BaseException(String message, Throwable cause, String errorCode, int httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = null;
    }

    /**
     * Constructs a new BaseException with the specified detail message and context.
     * 
     * @param message the detail message
     * @param errorCode the error code
     * @param httpStatus the HTTP status code
     * @param context additional context information
     */
    protected BaseException(String message, String errorCode, int httpStatus, Object context) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = context;
    }

    /**
     * Constructs a new BaseException with the specified detail message, cause, and context.
     * 
     * @param message the detail message
     * @param cause the cause
     * @param errorCode the error code
     * @param httpStatus the HTTP status code
     * @param context additional context information
     */
    protected BaseException(String message, Throwable cause, String errorCode, int httpStatus, Object context) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = context;
    }
}