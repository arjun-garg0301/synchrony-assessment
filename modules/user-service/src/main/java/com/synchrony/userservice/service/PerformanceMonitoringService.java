package com.synchrony.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for monitoring application performance metrics.
 * Tracks RPM and other performance indicators.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class PerformanceMonitoringService {

    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private volatile long lastResetTime = System.currentTimeMillis();

    /**
     * Increments the request counter.
     */
    public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }

    /**
     * Increments the error counter.
     */
    public void incrementErrorCount() {
        errorCount.incrementAndGet();
    }

    /**
     * Gets the current RPM (Requests Per Minute).
     * 
     * @return current RPM
     */
    public double getCurrentRPM() {
        long currentTime = System.currentTimeMillis();
        long elapsedMinutes = (currentTime - lastResetTime) / 60000;
        
        if (elapsedMinutes == 0) {
            return 0;
        }
        
        return (double) requestCount.get() / elapsedMinutes;
    }

    /**
     * Gets performance statistics.
     * 
     * @return performance stats as string
     */
    public String getPerformanceStats() {
        long currentTime = System.currentTimeMillis();
        long elapsedMinutes = (currentTime - lastResetTime) / 60000;
        double rpm = elapsedMinutes > 0 ? (double) requestCount.get() / elapsedMinutes : 0;
        double errorRate = requestCount.get() > 0 ? (double) errorCount.get() / requestCount.get() * 100 : 0;
        
        return String.format(
            "Performance Stats - RPM: %.2f, Total Requests: %d, Errors: %d, Error Rate: %.2f%%",
            rpm, requestCount.get(), errorCount.get(), errorRate
        );
    }

    /**
     * Resets the performance counters.
     */
    public void resetCounters() {
        requestCount.set(0);
        errorCount.set(0);
        lastResetTime = System.currentTimeMillis();
        log.info("Performance counters reset");
    }
}