package com.synchrony.userservice.service;

/**
 * Service interface for Kafka message production.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface KafkaProducerService {

    /**
     * Sends an image event message to Kafka.
     * 
     * @param username the username associated with the event
     * @param imageName the name of the image
     * @param eventType the type of event (e.g., IMAGE_UPLOADED, IMAGE_DELETED)
     */
    void sendImageEvent(String username, String imageName, String eventType);

    /**
     * Sends a user event message to Kafka.
     * 
     * @param username the username associated with the event
     * @param eventType the type of event (e.g., USER_REGISTERED, USER_UPDATED)
     */
    void sendUserEvent(String username, String eventType);
}