package com.synchrony.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

/**
 * OAuth2 Resource Server configuration.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class OAuth2Config {

    @Value("${app.jwt.secret:test-secret-key-for-jwt-token-generation-in-tests-should-be-long-enough}")
    private String jwtSecret;

    /**
     * JWT decoder for OAuth2 resource server.
     * Uses externalized configuration with fallback for test environments.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // Use externalized configuration for JWT secret with safe fallback
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}