package com.synchrony.common.scheduler;

import com.synchrony.common.util.DropboxTokenService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DropboxTokenScheduler {
    @Getter
    private static String currentAccessToken;
    @Autowired
    private DropboxTokenService tokenService;

    @PostConstruct
    public void init() {
        // Run the scheduler once on startup to initialize the access token
        refreshAccessToken();
    }

    @Scheduled(fixedRate = 1200000) // 20 minutes
    public void refreshAccessToken() {
        log.info("Scheduler triggered to refresh Dropbox access token");
        try {
            currentAccessToken = tokenService.getAccessToken();
            log.info("Access token refreshed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh Dropbox access token", e);
        }
    }
}