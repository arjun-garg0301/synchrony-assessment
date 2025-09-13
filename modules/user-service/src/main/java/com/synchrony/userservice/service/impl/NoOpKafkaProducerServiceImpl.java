package com.synchrony.userservice.service.impl;

import com.synchrony.userservice.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * No-op implementation of KafkaProducerService when Kafka is disabled.
 * This prevents errors and improves performance when Kafka is not available.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpKafkaProducerServiceImpl implements KafkaProducerService {

    @Override
    public void sendImageEvent(String username, String imageName, String eventType) {
        log.debug("Kafka is disabled - skipping image event: username={}, imageName={}, eventType={}", 
                username, imageName, eventType);
    }

    @Override
    public void sendUserEvent(String username, String eventType) {
        log.debug("Kafka is disabled - skipping user event: username={}, eventType={}", username, eventType);
    }
}