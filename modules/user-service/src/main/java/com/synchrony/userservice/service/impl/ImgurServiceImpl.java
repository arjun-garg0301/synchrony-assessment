package com.synchrony.userservice.service.impl;

import com.synchrony.common.exception.BusinessException;
import com.synchrony.userservice.dto.ImgurUploadResponse;
import com.synchrony.userservice.service.ImgurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * Implementation of ImgurService interface for Imgur API integration.
 * Enhanced with proper error handling and validation.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.imgur.enabled", havingValue = "true", matchIfMissing = true)
public class ImgurServiceImpl implements ImgurService {

    @Value("${app.imgur.base-url}")
    private String imgurBaseUrl;

    @Value("${app.imgur.client-id}")
    private String imgurClientId;

    @Value("${app.imgur.upload-endpoint}")
    private String uploadEndpoint;

    @Value("${app.imgur.delete-endpoint}")
    private String deleteEndpoint;

    private final RestTemplate restTemplate;

    public ImgurServiceImpl() {
        this.restTemplate = createOptimizedRestTemplate();
    }
    
    /**
     * Creates a RestTemplate with optimized timeout settings.
     */
    private RestTemplate createOptimizedRestTemplate() {
        // Create RestTemplate with timeout configuration
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        
        // Set timeouts to prevent long waits
        factory.setConnectTimeout(5000); // 5 seconds
        factory.setReadTimeout(10000);   // 10 seconds
        
        return new RestTemplate(factory);
    }

    @Override
    public ImgurUploadResponse uploadImage(MultipartFile file, String title, String description) {
        log.info("Uploading image to Imgur: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        
        try {
            // Validate file
            validateImageFile(file);
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Client-ID " + imgurClientId);
            
            // Convert file to base64
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            
            // Prepare request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", base64Image);
            body.add("type", "base64");
            
            if (title != null && !title.trim().isEmpty()) {
                body.add("title", title.trim());
            }
            if (description != null && !description.trim().isEmpty()) {
                body.add("description", description.trim());
            }
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Make API call
            String uploadUrl = imgurBaseUrl + uploadEndpoint;
            log.debug("Making request to Imgur API: {}", uploadUrl);
            
            ResponseEntity<ImgurUploadResponse> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    ImgurUploadResponse.class
            );
            
            ImgurUploadResponse uploadResponse = response.getBody();
            
            if (uploadResponse == null || !uploadResponse.isSuccess()) {
                log.error("Imgur upload failed: response={}", uploadResponse);
                throw new BusinessException("Failed to upload image to Imgur", "IMGUR_UPLOAD_FAILED");
            }
            
            log.info("Image uploaded successfully to Imgur: imageId={}", uploadResponse.getData().getId());
            return uploadResponse;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading image to Imgur: {}", e.getMessage(), e);
            throw new BusinessException("Failed to upload image to Imgur: " + e.getMessage(), "IMGUR_UPLOAD_ERROR", e);
        }
    }

    @Override
    public boolean deleteImage(String deleteHash) {
        log.info("Deleting image from Imgur: deleteHash={}", deleteHash);
        
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + imgurClientId);
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            // Make API call
            String deleteUrl = imgurBaseUrl + deleteEndpoint.replace("{deleteHash}", deleteHash);
            log.debug("Making delete request to Imgur API: {}", deleteUrl);
            
            ResponseEntity<ImgurUploadResponse> response = restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    ImgurUploadResponse.class
            );
            
            ImgurUploadResponse deleteResponse = response.getBody();
            boolean success = deleteResponse != null && deleteResponse.isSuccess();
            
            if (success) {
                log.info("Image deleted successfully from Imgur: deleteHash={}", deleteHash);
            } else {
                log.warn("Failed to delete image from Imgur: deleteHash={}, response={}", deleteHash, deleteResponse);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Error deleting image from Imgur: deleteHash={}, error={}", deleteHash, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ImgurUploadResponse getImageInfo(String imageId) {
        log.debug("Retrieving image info from Imgur: imageId={}", imageId);
        
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + imgurClientId);
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            // Make API call
            String infoUrl = imgurBaseUrl + "/image/" + imageId;
            log.debug("Making info request to Imgur API: {}", infoUrl);
            
            ResponseEntity<ImgurUploadResponse> response = restTemplate.exchange(
                    infoUrl,
                    HttpMethod.GET,
                    requestEntity,
                    ImgurUploadResponse.class
            );
            
            ImgurUploadResponse infoResponse = response.getBody();
            
            if (infoResponse == null || !infoResponse.isSuccess()) {
                log.warn("Failed to retrieve image info from Imgur: imageId={}, response={}", imageId, infoResponse);
                throw new BusinessException("Failed to retrieve image info from Imgur", "IMGUR_INFO_FAILED");
            }
            
            log.debug("Image info retrieved successfully from Imgur: imageId={}", imageId);
            return infoResponse;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving image info from Imgur: imageId={}, error={}", imageId, e.getMessage(), e);
            throw new BusinessException("Failed to retrieve image info from Imgur: " + e.getMessage(), "IMGUR_INFO_ERROR", e);
        }
    }

    /**
     * Validates the uploaded image file.
     * 
     * @param file the file to validate
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file is required", "FILE_REQUIRED");
        }
        
        // Check file size (10MB limit)
        long maxFileSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("Image file size exceeds 10MB limit", "FILE_TOO_LARGE");
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("File must be an image", "INVALID_FILE_TYPE");
        }
        
        // Check supported formats
        String[] supportedFormats = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
        boolean isSupported = false;
        for (String format : supportedFormats) {
            if (format.equals(contentType)) {
                isSupported = true;
                break;
            }
        }
        
        if (!isSupported) {
            throw new BusinessException("Unsupported image format. Supported formats: JPEG, PNG, GIF, WebP", "UNSUPPORTED_FORMAT");
        }
        
        log.debug("Image file validation passed: filename={}, size={}, contentType={}", 
                file.getOriginalFilename(), file.getSize(), contentType);
    }
}