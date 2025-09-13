package com.synchrony.userservice.interceptor;

import com.synchrony.userservice.service.PerformanceMonitoringService;
import com.synchrony.userservice.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for API rate limiting.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final PerformanceMonitoringService performanceMonitoringService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Track request for performance monitoring
        performanceMonitoringService.incrementRequestCount();
        
        String clientIp = getClientIpAddress(request);
        String path = request.getRequestURI();
        
        // Skip rate limiting for performance and health endpoints
        if (path.contains("/performance/health")) {
            return true;
        }
        
        // Determine bucket type based on path
        RateLimitService.BucketType bucketType = determineBucketType(path);
        
        // Create rate limit key (IP + bucket type)
        String rateLimitKey = clientIp + ":" + bucketType.name();
        
        // Check rate limit
        if (!rateLimitService.isAllowed(rateLimitKey, bucketType)) {
            performanceMonitoringService.incrementErrorCount();
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests\"}");
            return false;
        }
        
        // Add rate limit headers
        long availableTokens = rateLimitService.getAvailableTokens(rateLimitKey, bucketType);
        response.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));
        response.setHeader("X-RateLimit-Limit", String.valueOf(getRateLimitForBucketType(bucketType)));
        
        return true;
    }

    /**
     * Determines the bucket type based on the request path.
     */
    private RateLimitService.BucketType determineBucketType(String path) {
        if (path.contains("/auth/")) {
            return RateLimitService.BucketType.AUTH;
        } else if (path.contains("/images/upload") || path.contains("/dropbox/upload")) {
            return RateLimitService.BucketType.IMAGE_UPLOAD;
        } else {
            return RateLimitService.BucketType.API;
        }
    }

    /**
     * Gets the rate limit for a bucket type.
     */
    private int getRateLimitForBucketType(RateLimitService.BucketType bucketType) {
        return switch (bucketType) {
            case API -> 1000;
            case AUTH -> 100;
            case IMAGE_UPLOAD -> 200;
        };
    }

    /**
     * Extracts the client IP address from the request.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}