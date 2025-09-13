package com.synchrony.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.userservice.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of KafkaProducerService interface.
 * Enhanced with proper error handling and message structure.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Value("${app.kafka.topic.image-events}")
    private String imageEventsTopic;

    @Value("${app.kafka.topic.user-events}")
    private String userEventsTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    // Circuit breaker pattern
    private final AtomicBoolean circuitOpen = new AtomicBoolean(false);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private static final long CIRCUIT_BREAKER_TIMEOUT = TimeUnit.MINUTES.toMillis(5); // 5 minutes

    @Autowired
    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendImageEvent(String username, String imageName, String eventType) {
        if (!isCircuitClosed()) {
            log.debug("Kafka circuit breaker is open, skipping image event: username={}, eventType={}", username, eventType);
            return;
        }
        
        log.debug("Sending image event: username={}, imageName={}, eventType={}", username, imageName, eventType);
        
        try {
            Map<String, Object> eventData = createEventData(username, eventType);
            eventData.put("imageName", imageName);
            eventData.put("eventCategory", "IMAGE");
            
            String message = objectMapper.writeValueAsString(eventData);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(imageEventsTopic, username, message);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.debug("Image event sent successfully: username={}, eventType={}", username, eventType);
                    resetCircuitBreaker();
                } else {
                    log.warn("Failed to send image event: username={}, eventType={}, error={}", 
                            username, eventType, exception.getMessage());
                    openCircuitBreaker();
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing image event message: username={}, eventType={}, error={}", 
                    username, eventType, e.getMessage());
        } catch (Exception e) {
            log.warn("Unexpected error sending image event: username={}, eventType={}, error={}", 
                    username, eventType, e.getMessage());
            openCircuitBreaker();
        }
    }

    @Override
    public void sendUserEvent(String username, String eventType) {
        if (!isCircuitClosed()) {
            log.debug("Kafka circuit breaker is open, skipping user event: username={}, eventType={}", username, eventType);
            return;
        }
        
        log.debug("Sending user event: username={}, eventType={}", username, eventType);
        
        try {
            Map<String, Object> eventData = createEventData(username, eventType);
            eventData.put("eventCategory", "USER");
            
            String message = objectMapper.writeValueAsString(eventData);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(userEventsTopic, username, message);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.debug("User event sent successfully: username={}, eventType={}", username, eventType);
                    resetCircuitBreaker();
                } else {
                    log.warn("Failed to send user event: username={}, eventType={}, error={}", 
                            username, eventType, exception.getMessage());
                    openCircuitBreaker();
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing user event message: username={}, eventType={}, error={}", 
                    username, eventType, e.getMessage());
        } catch (Exception e) {
            log.warn("Unexpected error sending user event: username={}, eventType={}, error={}", 
                    username, eventType, e.getMessage());
            openCircuitBreaker();
        }
    }

    /**
     * Creates common event data structure.
     * 
     * @param username the username
     * @param eventType the event type
     * @return event data map
     */
    private Map<String, Object> createEventData(String username, String eventType) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("username", username);
        eventData.put("eventType", eventType);
        eventData.put("timestamp", LocalDateTime.now().toString());
        eventData.put("source", "user-service");
        eventData.put("version", "1.0.0");
        return eventData;
    }
    
    /**
     * Checks if circuit breaker is closed (allowing requests).
     */
    private boolean isCircuitClosed() {
        if (!circuitOpen.get()) {
            return true;
        }
        
        // Check if timeout has passed to reset circuit breaker
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFailureTime.get() > CIRCUIT_BREAKER_TIMEOUT) {
            resetCircuitBreaker();
            return true;
        }
        
        return false;
    }
    
    /**
     * Opens the circuit breaker.
     */
    private void openCircuitBreaker() {
        circuitOpen.set(true);
        lastFailureTime.set(System.currentTimeMillis());
        log.warn("Kafka circuit breaker opened due to failures");
    }
    
    /**
     * Resets the circuit breaker.
     */
    private void resetCircuitBreaker() {
        if (circuitOpen.get()) {
            circuitOpen.set(false);
            log.info("Kafka circuit breaker reset - connection restored");
        }
    }
}