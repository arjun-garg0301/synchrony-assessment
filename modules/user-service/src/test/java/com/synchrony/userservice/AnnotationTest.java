package com.synchrony.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple test to verify annotations work correctly.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnnotationTest {

    @Test
    public void testAnnotationsWork() {
        // If this test runs, it means @AutoConfigureTestMvc is working
        assertTrue(true, "Annotations are working correctly");
    }
}