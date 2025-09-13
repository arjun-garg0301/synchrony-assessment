package com.synchrony.userservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Performance optimization configuration for 100K RPM.
 * Includes Caffeine caching, connection pooling, and async processing.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableCaching
@EnableAsync
public class PerformanceConfig {

    /**
     * High-performance Caffeine cache manager for in-memory caching.
     * Optimized for 100K RPM with automatic eviction and statistics.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure Caffeine cache with high-performance settings
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000)                    // Maximum 10K entries per cache
                .expireAfterWrite(Duration.ofMinutes(30))  // 30 minutes TTL
                .expireAfterAccess(Duration.ofMinutes(10)) // 10 minutes idle timeout
                .recordStats()                         // Enable statistics for monitoring
                .initialCapacity(100)                  // Initial capacity
        );
        
        // Pre-configure cache names for better performance
        cacheManager.setCacheNames(List.of("users", "images", "tokens", "userImages", "imageDetails"));
        
        return cacheManager;
    }

    /**
     * High-performance async executor for non-blocking operations.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);     // Core threads for high load
        executor.setMaxPoolSize(200);     // Maximum threads
        executor.setQueueCapacity(1000);  // Queue capacity
        executor.setKeepAliveSeconds(60); // Keep alive time
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * Dedicated executor for image processing operations.
     */
    @Bean(name = "imageProcessingExecutor")
    public Executor imageProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("image-proc-");
        executor.initialize();
        return executor;
    }

    /**
     * Dedicated executor for Kafka operations.
     */
    @Bean(name = "kafkaExecutor")
    public Executor kafkaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("kafka-");
        executor.initialize();
        return executor;
    }
}