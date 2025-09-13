package com.synchrony.userservice.service;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.synchrony.userservice.config.RateLimitConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing API rate limiting.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final ConcurrentHashMap<String, Bucket> bucketCache;
    private final RateLimitConfig rateLimitConfig;

    /**
     * Checks if the request is allowed based on rate limiting.
     * 
     * @param key the rate limit key (user ID, IP, etc.)
     * @param bucketType the type of bucket to use
     * @return true if request is allowed, false otherwise
     */
    public boolean isAllowed(String key, BucketType bucketType) {
        Bucket bucket = bucketCache.computeIfAbsent(key, k -> createBucket(bucketType));
        boolean allowed = bucket.tryConsume(1);
        
        if (!allowed) {
            log.warn("Rate limit exceeded for key: {} with bucket type: {}", key, bucketType);
        }
        
        return allowed;
    }

    /**
     * Gets the number of available tokens for a key.
     * 
     * @param key the rate limit key
     * @param bucketType the type of bucket
     * @return number of available tokens
     */
    public long getAvailableTokens(String key, BucketType bucketType) {
        Bucket bucket = bucketCache.computeIfAbsent(key, k -> createBucket(bucketType));
        return bucket.getAvailableTokens();
    }

    /**
     * Creates a bucket based on the bucket type.
     * 
     * @param bucketType the type of bucket to create
     * @return the created bucket
     */
    private Bucket createBucket(BucketType bucketType) {
        return switch (bucketType) {
            case API -> rateLimitConfig.createApiRateLimitBucket();
            case AUTH -> rateLimitConfig.createAuthRateLimitBucket();
            case IMAGE_UPLOAD -> rateLimitConfig.createImageUploadRateLimitBucket();
        };
    }

    /**
     * Enum for different bucket types.
     */
    public enum BucketType {
        API,
        AUTH,
        IMAGE_UPLOAD
    }
}