package com.synchrony.common.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility class for managing correlation IDs across requests.
 * Provides functionality to generate, set, and retrieve correlation IDs for request tracing.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class CorrelationIdUtil {

    /**
     * MDC key for correlation ID.
     */
    public static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * HTTP header name for correlation ID.
     */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private CorrelationIdUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Generates a new correlation ID.
     * 
     * @return new correlation ID
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sets the correlation ID in MDC.
     * 
     * @param correlationId the correlation ID to set
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
    }

    /**
     * Gets the current correlation ID from MDC.
     * 
     * @return current correlation ID, or null if not set
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    /**
     * Gets the current correlation ID from MDC, generating a new one if not present.
     * 
     * @return current or new correlation ID
     */
    public static String getOrGenerateCorrelationId() {
        String correlationId = getCorrelationId();
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = generateCorrelationId();
            setCorrelationId(correlationId);
        }
        return correlationId;
    }

    /**
     * Clears the correlation ID from MDC.
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    /**
     * Executes a runnable with a specific correlation ID.
     * 
     * @param correlationId the correlation ID to use
     * @param runnable the runnable to execute
     */
    public static void executeWithCorrelationId(String correlationId, Runnable runnable) {
        String previousCorrelationId = getCorrelationId();
        try {
            setCorrelationId(correlationId);
            runnable.run();
        } finally {
            if (previousCorrelationId != null) {
                setCorrelationId(previousCorrelationId);
            } else {
                clearCorrelationId();
            }
        }
    }
}