package com.synchrony.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil class.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ValidationUtilTest {

    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        validationUtil = new ValidationUtil();
    }

    @Test
    void testIsValidEmail_ValidEmails() {
        // Valid email addresses
        assertTrue(validationUtil.isValidEmail("test@example.com"));
        assertTrue(validationUtil.isValidEmail("user.name@domain.co.uk"));
        assertTrue(validationUtil.isValidEmail("user+tag@example.org"));
        assertTrue(validationUtil.isValidEmail("123@example.com"));
        assertTrue(validationUtil.isValidEmail("test_email@example-domain.com"));
    }

    @Test
    void testIsValidEmail_InvalidEmails() {
        // Invalid email addresses
        assertFalse(validationUtil.isValidEmail("invalid-email"));
        assertFalse(validationUtil.isValidEmail("@example.com"));
        assertFalse(validationUtil.isValidEmail("test@"));
        assertFalse(validationUtil.isValidEmail("test..test@example.com"));
        assertFalse(validationUtil.isValidEmail("test@example"));
        assertFalse(validationUtil.isValidEmail(""));
        assertFalse(validationUtil.isValidEmail(null));
        assertFalse(validationUtil.isValidEmail("   "));
    }

    @Test
    void testIsValidUsername_ValidUsernames() {
        // Valid usernames
        assertTrue(validationUtil.isValidUsername("user123"));
        assertTrue(validationUtil.isValidUsername("test_user"));
        assertTrue(validationUtil.isValidUsername("User"));
        assertTrue(validationUtil.isValidUsername("abc"));
        assertTrue(validationUtil.isValidUsername("12345678901234567890")); // 20 chars
    }

    @Test
    void testIsValidUsername_InvalidUsernames() {
        // Invalid usernames
        assertFalse(validationUtil.isValidUsername("ab")); // too short
        assertFalse(validationUtil.isValidUsername("123456789012345678901")); // too long
        assertFalse(validationUtil.isValidUsername("user-name")); // contains dash
        assertFalse(validationUtil.isValidUsername("user name")); // contains space
        assertFalse(validationUtil.isValidUsername("user@name")); // contains @
        assertFalse(validationUtil.isValidUsername(""));
        assertFalse(validationUtil.isValidUsername(null));
        assertFalse(validationUtil.isValidUsername("   "));
    }

    @Test
    void testIsValidPassword_ValidPasswords() {
        // Valid passwords (8+ characters)
        assertTrue(validationUtil.isValidPassword("password123"));
        assertTrue(validationUtil.isValidPassword("12345678"));
        assertTrue(validationUtil.isValidPassword("a1b2c3d4"));
        assertTrue(validationUtil.isValidPassword("verylongpassword"));
    }

    @Test
    void testIsValidPassword_InvalidPasswords() {
        // Invalid passwords (less than 8 characters)
        assertFalse(validationUtil.isValidPassword("1234567")); // 7 chars
        assertFalse(validationUtil.isValidPassword("short"));
        assertFalse(validationUtil.isValidPassword(""));
        assertFalse(validationUtil.isValidPassword(null));
    }

    @Test
    void testIsStrongPassword_ValidStrongPasswords() {
        // Strong passwords (8+ chars with uppercase, lowercase, digit, special char)
        assertTrue(validationUtil.isStrongPassword("Password123!"));
        assertTrue(validationUtil.isStrongPassword("MyP@ssw0rd"));
        assertTrue(validationUtil.isStrongPassword("Str0ng&P@ss"));
    }

    @Test
    void testIsStrongPassword_InvalidStrongPasswords() {
        // Weak passwords
        assertFalse(validationUtil.isStrongPassword("password")); // no uppercase, digit, special
        assertFalse(validationUtil.isStrongPassword("PASSWORD")); // no lowercase, digit, special
        assertFalse(validationUtil.isStrongPassword("Password")); // no digit, special
        assertFalse(validationUtil.isStrongPassword("Password123")); // no special char
        assertFalse(validationUtil.isStrongPassword("Pass1!")); // too short
        assertFalse(validationUtil.isStrongPassword(""));
        assertFalse(validationUtil.isStrongPassword(null));
    }

    @Test
    void testIsValidPhoneNumber_ValidPhoneNumbers() {
        // Valid phone numbers
        assertTrue(validationUtil.isValidPhoneNumber("+1234567890"));
        assertTrue(validationUtil.isValidPhoneNumber("123-456-7890"));
        assertTrue(validationUtil.isValidPhoneNumber("(123) 456-7890"));
        assertTrue(validationUtil.isValidPhoneNumber("1234567890"));
        assertTrue(validationUtil.isValidPhoneNumber("+44 20 7946 0958"));
        assertTrue(validationUtil.isValidPhoneNumber(null)); // null is valid (optional)
        assertTrue(validationUtil.isValidPhoneNumber("")); // empty is valid (optional)
    }

    @Test
    void testIsValidPhoneNumber_InvalidPhoneNumbers() {
        // Invalid phone numbers
        assertFalse(validationUtil.isValidPhoneNumber("123")); // too short
        assertFalse(validationUtil.isValidPhoneNumber("abc-def-ghij")); // contains letters
        assertFalse(validationUtil.isValidPhoneNumber("123456789012345678901")); // too long
    }

    @Test
    void testIsNotNullOrEmpty() {
        // Valid non-empty strings
        assertTrue(validationUtil.isNotNullOrEmpty("test"));
        assertTrue(validationUtil.isNotNullOrEmpty("   test   "));
        assertTrue(validationUtil.isNotNullOrEmpty("123"));
        
        // Invalid empty/null strings
        assertFalse(validationUtil.isNotNullOrEmpty(null));
        assertFalse(validationUtil.isNotNullOrEmpty(""));
        assertFalse(validationUtil.isNotNullOrEmpty("   "));
    }

    @Test
    void testIsNullOrEmpty() {
        // Valid empty/null strings
        assertTrue(validationUtil.isNullOrEmpty(null));
        assertTrue(validationUtil.isNullOrEmpty(""));
        assertTrue(validationUtil.isNullOrEmpty("   "));
        
        // Invalid non-empty strings
        assertFalse(validationUtil.isNullOrEmpty("test"));
        assertFalse(validationUtil.isNullOrEmpty("   test   "));
        assertFalse(validationUtil.isNullOrEmpty("123"));
    }

    @Test
    void testIsValidLength() {
        // Valid lengths
        assertTrue(validationUtil.isValidLength("test", 1, 10));
        assertTrue(validationUtil.isValidLength("hello", 5, 5));
        assertTrue(validationUtil.isValidLength("", 0, 5));
        assertTrue(validationUtil.isValidLength("   test   ", 1, 10)); // trimmed length
        
        // Invalid lengths
        assertFalse(validationUtil.isValidLength("test", 5, 10)); // too short
        assertFalse(validationUtil.isValidLength("very long string", 1, 5)); // too long
        assertFalse(validationUtil.isValidLength(null, 1, 5)); // null with min > 0
        assertTrue(validationUtil.isValidLength(null, 0, 5)); // null with min = 0
    }

    @Test
    void testIsWithinRange() {
        // Valid ranges
        assertTrue(validationUtil.isWithinRange(5, 1, 10));
        assertTrue(validationUtil.isWithinRange(1, 1, 10)); // boundary
        assertTrue(validationUtil.isWithinRange(10, 1, 10)); // boundary
        assertTrue(validationUtil.isWithinRange(5.5, 1.0, 10.0));
        
        // Invalid ranges
        assertFalse(validationUtil.isWithinRange(0, 1, 10)); // below min
        assertFalse(validationUtil.isWithinRange(11, 1, 10)); // above max
        assertFalse(validationUtil.isWithinRange(null, 1, 10)); // null value
    }

    @Test
    void testMatchesPattern() {
        Pattern digitPattern = Pattern.compile("\\d+");
        
        // Valid matches
        assertTrue(validationUtil.matchesPattern("123", digitPattern));
        assertTrue(validationUtil.matchesPattern("456789", digitPattern));
        
        // Invalid matches
        assertFalse(validationUtil.matchesPattern("abc", digitPattern));
        assertFalse(validationUtil.matchesPattern("123abc", digitPattern));
        assertFalse(validationUtil.matchesPattern("", digitPattern));
        assertFalse(validationUtil.matchesPattern(null, digitPattern));
        assertFalse(validationUtil.matchesPattern("123", null));
    }

    @Test
    void testIsAlphanumeric() {
        // Valid alphanumeric strings
        assertTrue(validationUtil.isAlphanumeric("abc123"));
        assertTrue(validationUtil.isAlphanumeric("ABC"));
        assertTrue(validationUtil.isAlphanumeric("123"));
        assertTrue(validationUtil.isAlphanumeric("Test123"));
        
        // Invalid alphanumeric strings
        assertFalse(validationUtil.isAlphanumeric("test-123"));
        assertFalse(validationUtil.isAlphanumeric("test 123"));
        assertFalse(validationUtil.isAlphanumeric("test@123"));
        assertFalse(validationUtil.isAlphanumeric(""));
        assertFalse(validationUtil.isAlphanumeric(null));
        assertFalse(validationUtil.isAlphanumeric("   "));
    }

    @Test
    void testIsValidUrl() {
        // Valid URLs
        assertTrue(validationUtil.isValidUrl("http://example.com"));
        assertTrue(validationUtil.isValidUrl("https://www.example.com"));
        assertTrue(validationUtil.isValidUrl("https://example.com/path?param=value"));
        assertTrue(validationUtil.isValidUrl("ftp://ftp.example.com"));
        
        // Invalid URLs
        assertFalse(validationUtil.isValidUrl("not-a-url"));
        assertFalse(validationUtil.isValidUrl("http://"));
        assertFalse(validationUtil.isValidUrl(""));
        assertFalse(validationUtil.isValidUrl(null));
        assertFalse(validationUtil.isValidUrl("   "));
    }
}