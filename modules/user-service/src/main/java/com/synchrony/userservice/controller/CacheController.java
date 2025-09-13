package com.synchrony.userservice.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.synchrony.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for cache management and monitoring.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/cache")
@RequiredArgsConstructor
@Tag(name = "Cache Management", description = "Cache management and monitoring APIs")
public class CacheController {

    private final CacheManager cacheManager;

    /**
     * Gets cache statistics for all caches.
     * 
     * @return ResponseEntity containing cache statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get cache statistics", description = "Retrieve statistics for all caches")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats() {
        log.debug("Retrieving cache statistics");
        
        Map<String, Object> allStats = new HashMap<>();
        
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();
                
                Map<String, Object> cacheStats = new HashMap<>();
                cacheStats.put("size", nativeCache.estimatedSize());
                cacheStats.put("hitCount", stats.hitCount());
                cacheStats.put("missCount", stats.missCount());
                cacheStats.put("hitRate", stats.hitRate());
                cacheStats.put("evictionCount", stats.evictionCount());
                cacheStats.put("averageLoadPenalty", stats.averageLoadPenalty());
                
                allStats.put(cacheName, cacheStats);
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success(allStats, "Cache statistics retrieved successfully"));
    }

    /**
     * Gets statistics for a specific cache.
     * 
     * @param cacheName the name of the cache
     * @return ResponseEntity containing cache statistics
     */
    @GetMapping("/stats/{cacheName}")
    @Operation(summary = "Get specific cache statistics", description = "Retrieve statistics for a specific cache")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats(@PathVariable String cacheName) {
        log.debug("Retrieving cache statistics for: {}", cacheName);
        
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> cacheStats = new HashMap<>();
        
        if (cache instanceof CaffeineCache) {
            CaffeineCache caffeineCache = (CaffeineCache) cache;
            Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            CacheStats stats = nativeCache.stats();
            
            cacheStats.put("name", cacheName);
            cacheStats.put("size", nativeCache.estimatedSize());
            cacheStats.put("hitCount", stats.hitCount());
            cacheStats.put("missCount", stats.missCount());
            cacheStats.put("hitRate", stats.hitRate());
            cacheStats.put("evictionCount", stats.evictionCount());
            cacheStats.put("averageLoadPenalty", stats.averageLoadPenalty());
        }
        
        return ResponseEntity.ok(ApiResponse.success(cacheStats, "Cache statistics retrieved successfully"));
    }

    /**
     * Clears all caches.
     * 
     * @return ResponseEntity with success message
     */
    @PostMapping("/clear")
    @Operation(summary = "Clear all caches", description = "Clear all cache entries")
    public ResponseEntity<ApiResponse<Void>> clearAllCaches() {
        log.info("Clearing all caches");
        
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success(null, "All caches cleared successfully"));
    }

    /**
     * Clears a specific cache.
     * 
     * @param cacheName the name of the cache to clear
     * @return ResponseEntity with success message
     */
    @PostMapping("/clear/{cacheName}")
    @Operation(summary = "Clear specific cache", description = "Clear entries from a specific cache")
    public ResponseEntity<ApiResponse<Void>> clearCache(@PathVariable String cacheName) {
        log.info("Clearing cache: {}", cacheName);
        
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        cache.clear();
        
        return ResponseEntity.ok(ApiResponse.success(null, "Cache cleared successfully: " + cacheName));
    }

    /**
     * Gets cache names and basic info.
     * 
     * @return ResponseEntity containing cache information
     */
    @GetMapping("/info")
    @Operation(summary = "Get cache information", description = "Retrieve basic information about all caches")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheInfo() {
        log.debug("Retrieving cache information");
        
        Map<String, Object> info = new HashMap<>();
        info.put("cacheNames", cacheManager.getCacheNames());
        info.put("cacheCount", cacheManager.getCacheNames().size());
        info.put("cacheType", "Caffeine");
        
        return ResponseEntity.ok(ApiResponse.success(info, "Cache information retrieved successfully"));
    }
}