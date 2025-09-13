package com.synchrony.userservice.config;

import com.synchrony.userservice.interceptor.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for interceptors and other web-related settings.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;
    
    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (rateLimitEnabled) {
            registry.addInterceptor(rateLimitInterceptor)
                    .addPathPatterns("/v1/**")  // Apply to all API endpoints
                    .excludePathPatterns(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/h2-console/**",
                        "/v1/performance/**",
                        "/v1/cache/**"
                    );
        }
    }
}