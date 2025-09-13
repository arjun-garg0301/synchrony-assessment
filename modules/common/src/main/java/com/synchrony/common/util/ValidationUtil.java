package com.synchrony.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations across the platform.
 * Provides reusable validation methods with consistent behavior.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class ValidationUtil {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]{10,20}$");
    
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * Validates email format.
     * 
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        try {
            if (isNullOrEmpty(email)) {
                log.debug("Email validation failed: email is null or empty");
                return false;
            }
            
            boolean isValid = EMAIL_PATTERN.matcher(email.trim()).matches();
            log.debug("Email validation for '{}': {}", email, isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Error validating email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates username format.
     * Username should be 3-20 characters long and contain only alphanumeric characters and underscores.
     * 
     * @param username the username to validate
     * @return true if username is valid, false otherwise
     */
    public boolean isValidUsername(String username) {
        try {
            if (isNullOrEmpty(username)) {
                log.debug("Username validation failed: username is null or empty");
                return false;
            }
            
            boolean isValid = USERNAME_PATTERN.matcher(username.trim()).matches();
            log.debug("Username validation for '{}': {}", username, isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Error validating username: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates password strength.
     * Password should be at least 8 characters long.
     * 
     * @param password the password to validate
     * @return true if password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        try {
            if (isNullOrEmpty(password)) {
                log.debug("Password validation failed: password is null or empty");
                return false;
            }
            
            boolean isValid = password.length() >= 8;
            log.debug("Password validation: length check passed = {}", isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Error validating password: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates strong password format.
     * Password must contain at least 8 characters with uppercase, lowercase, digit, and special character.
     * 
     * @param password the password to validate
     * @return true if password is strong, false otherwise
     */
    public boolean isStrongPassword(String password) {
        try {
            if (isNullOrEmpty(password)) {
                log.debug("Strong password validation failed: password is null or empty");
                return false;
            }
            
            boolean isValid = STRONG_PASSWORD_PATTERN.matcher(password).matches();
            log.debug("Strong password validation: {}", isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Error validating strong password: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates phone number format.
     * 
     * @param phoneNumber the phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        try {
            if (isNullOrEmpty(phoneNumber)) {
                return true; // Phone number is optional
            }
            
            boolean isValid = PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
            log.debug("Phone number validation for '{}': {}", phoneNumber, isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Error validating phone number: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates if a string is not null or empty.
     * 
     * @param value the string to validate
     * @return true if string is not null or empty, false otherwise
     */
    public boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates if a string is null or empty.
     * 
     * @param value the string to validate
     * @return true if string is null or empty, false otherwise
     */
    public boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validates string length within specified bounds.
     * 
     * @param value the string to validate
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return true if length is within bounds, false otherwise
     */
    public boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return minLength == 0;
        }
        
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validates if a value is within numeric range.
     * 
     * @param value the value to validate
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return true if value is within range, false otherwise
     */
    public boolean isWithinRange(Number value, Number min, Number max) {
        if (value == null) {
            return false;
        }
        
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        
        return val >= minVal && val <= maxVal;
    }

    /**
     * Validates if a string matches a custom pattern.
     * 
     * @param value the string to validate
     * @param pattern the regex pattern
     * @return true if string matches pattern, false otherwise
     */
    public boolean matchesPattern(String value, Pattern pattern) {
        try {
            if (isNullOrEmpty(value) || pattern == null) {
                return false;
            }
            
            return pattern.matcher(value.trim()).matches();
            
        } catch (Exception e) {
            log.error("Error validating pattern: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates if a string contains only alphanumeric characters.
     * 
     * @param value the string to validate
     * @return true if string is alphanumeric, false otherwise
     */
    public boolean isAlphanumeric(String value) {
        if (isNullOrEmpty(value)) {
            return false;
        }
        
        return value.trim().matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Validates if a string is a valid URL.
     * 
     * @param url the URL to validate
     * @return true if URL is valid, false otherwise
     */
    public boolean isValidUrl(String url) {
        try {
            if (isNullOrEmpty(url)) {
                return false;
            }
            
            String trimmedUrl = url.trim();
            if (trimmedUrl.isEmpty()) {
                return false;
            }
            
            java.net.URL urlObj = new java.net.URL(trimmedUrl);
            // Additional validation to ensure it's a proper URL
            String protocol = urlObj.getProtocol();
            String host = urlObj.getHost();
            
            // Must have a valid protocol and host
            return protocol != null && !protocol.isEmpty() && 
                   host != null && !host.isEmpty();
            
        } catch (Exception e) {
            log.debug("URL validation failed for '{}': {}", url, e.getMessage());
            return false;
        }
    }
}