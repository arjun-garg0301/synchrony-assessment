package com.synchrony.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for the User Service Application.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTest {

    /**
     * Test that the application context loads successfully.
     */
    @Test
    void contextLoads() {
        // This test will pass if the application context loads without errors
    }
}