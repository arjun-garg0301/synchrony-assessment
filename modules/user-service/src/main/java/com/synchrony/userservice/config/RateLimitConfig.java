package com.synchrony.userservice.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration for API endpoints.
 * Implements token bucket algorithm for 100K RPM optimization.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RateLimitConfig {

    /**
     * Bucket cache for storing rate limit buckets per user/IP.
     */
    @Bean
    public ConcurrentHashMap<String, Bucket> bucketCache() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a rate limit bucket for general API endpoints.
     * Allows 1000 requests per minute per user.
     */
    public Bucket createApiRateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Creates a rate limit bucket for authentication endpoints.
     * Allows 100 requests per minute per IP to prevent brute force.
     */
    public Bucket createAuthRateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Creates a rate limit bucket for image upload endpoints.
     * Allows 200 requests per minute per user.
     */
    public Bucket createImageUploadRateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}