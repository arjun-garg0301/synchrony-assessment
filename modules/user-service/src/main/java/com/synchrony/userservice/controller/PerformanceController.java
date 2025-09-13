package com.synchrony.userservice.controller;

import com.synchrony.common.dto.ApiResponse;
import com.synchrony.userservice.service.PerformanceMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for performance monitoring and metrics.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/performance")
@RequiredArgsConstructor
@Tag(name = "Performance Monitoring", description = "Performance monitoring and metrics APIs")
public class PerformanceController {

    private final PerformanceMonitoringService performanceMonitoringService;

    /**
     * Gets current performance metrics including RPM.
     * 
     * @return ResponseEntity containing performance metrics
     */
    @GetMapping("/metrics")
    @Operation(summary = "Get performance metrics", description = "Retrieve current performance metrics including RPM")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        log.debug("Retrieving performance metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("currentRPM", performanceMonitoringService.getCurrentRPM());
        metrics.put("stats", performanceMonitoringService.getPerformanceStats());
        metrics.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success(metrics, "Performance metrics retrieved successfully"));
    }

    /**
     * Resets performance counters.
     * 
     * @return ResponseEntity with success message
     */
    @PostMapping("/reset")
    @Operation(summary = "Reset performance counters", description = "Reset all performance counters and metrics")
    public ResponseEntity<ApiResponse<Void>> resetPerformanceCounters() {
        log.info("Resetting performance counters");
        
        performanceMonitoringService.resetCounters();
        
        return ResponseEntity.ok(ApiResponse.success(null, "Performance counters reset successfully"));
    }

    /**
     * Health check endpoint optimized for high throughput.
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    @Operation(summary = "High-performance health check", description = "Lightweight health check for load balancers")
    public ResponseEntity<Map<String, String>> healthCheck() {
        // Lightweight health check without logging to reduce overhead
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(health);
    }
}