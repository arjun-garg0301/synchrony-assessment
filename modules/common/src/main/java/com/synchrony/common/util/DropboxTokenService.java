package com.synchrony.common.util;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class DropboxTokenService {

    private static final String TOKEN_URL = "https://api.dropboxapi.com/oauth2/token";
    private static final String CLIENT_ID = "xke2z8xjt82k36o";
    private static final String CLIENT_SECRET = "37dpdtdw9ibhxki";
    private static final String REFRESH_TOKEN = "Qy7Lb0n-OAkAAAAAAAAAAZMSYxjCmXAWYI08NNmGSM_HvAO6k0jnv6qTbxbEybnO";

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = String.format(
                "grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s",
                REFRESH_TOKEN, CLIENT_ID, CLIENT_SECRET);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    TOKEN_URL, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                assert response.getBody() != null;
                return (String) response.getBody().get("access_token");
            } else {
                throw new RuntimeException("Failed to refresh token: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while refreshing token: " + e.getMessage());
        }
    }
}