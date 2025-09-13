package com.synchrony.userservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Enhanced with proper error handling and security features.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Generates a JWT token for the given username.
     * 
     * @param username the username
     * @return JWT token
     */
    public String generateToken(String username) {
        log.debug("Generating JWT token for username: {}", username);
        
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", username);
            claims.put("iat", System.currentTimeMillis() / 1000);
            return createToken(claims, username);
        } catch (Exception e) {
            log.error("Error generating JWT token for username '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Generates a JWT token with custom claims.
     * 
     * @param extraClaims additional claims to include
     * @param username the username
     * @return JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        log.debug("Generating JWT token with extra claims for username: {}", username);
        
        try {
            return createToken(extraClaims, username);
        } catch (Exception e) {
            log.error("Error generating JWT token with extra claims for username '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Extracts username from JWT token.
     * 
     * @param token the JWT token
     * @return username
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("Error extracting username from JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts expiration date from JWT token.
     * 
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            log.error("Error extracting expiration from JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts a specific claim from JWT token.
     * 
     * @param token the JWT token
     * @param claimsResolver function to extract the claim
     * @param <T> the type of the claim
     * @return the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            log.error("Error extracting claim from JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract claim from JWT token", e);
        }
    }

    /**
     * Validates JWT token against user details.
     * 
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            log.debug("JWT token validation result for username '{}': {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates JWT token.
     * 
     * @param token the JWT token
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            boolean isValid = !isTokenExpired(token);
            log.debug("JWT token validation result: {}", isValid);
            return isValid;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.warn("JWT security validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if JWT token is expired.
     * 
     * @param token the JWT token
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking if JWT token is expired: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Creates a JWT token.
     * 
     * @param claims the claims to include
     * @param subject the subject (username)
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts all claims from JWT token.
     * 
     * @param token the JWT token
     * @return claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gets the signing key for JWT operations.
     * 
     * @return signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}