package com.synchrony.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the User Service.
 * This microservice provides comprehensive user management and image handling capabilities
 * with Imgur API integration, JWT-based authentication, and event-driven architecture.
 * 
 * Features:
 * - User registration and authentication
 * - Image upload, management, and deletion via Imgur API
 * - JWT-based security with OAuth2
 * - Event publishing via Kafka
 * - RESTful API with OpenAPI documentation
 * - Comprehensive error handling and validation
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {
        "com.synchrony.userservice",
        "com.synchrony.common"
})
@EnableJpaRepositories(basePackages = "com.synchrony.userservice.repository")
@EntityScan(basePackages = "com.synchrony.userservice.entity")
@EnableJpaAuditing
@EnableTransactionManagement
@EnableKafka
@EnableCaching
@EnableAsync
@EnableScheduling
public class UserServiceApplication {

    /**
     * Main method to start the User Service application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}