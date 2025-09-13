package com.synchrony.userservice.controller;

import com.synchrony.common.dto.ApiResponse;
import com.synchrony.userservice.dto.ImageResponse;
import com.synchrony.userservice.dto.ImageUploadRequest;
import com.synchrony.userservice.service.ImageService;
import com.synchrony.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for image management operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "Image management APIs")
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    /**
     * Uploads an image for a user.
     * 
     * @param userId the user ID
     * @param file the image file to upload
     * @param title the image title (optional)
     * @param description the image description (optional)
     * @return ResponseEntity containing the uploaded image response
     */
    @PostMapping(value = "/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image", description = "Upload an image for a user")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        log.info("Image upload request for user ID: {}", userId);
        
        // Get username from userId
        String username = userService.getUserById(userId).getUsername();
        
        ImageUploadRequest uploadRequest = ImageUploadRequest.builder()
                .title(title)
                .description(description)
                .build();
        
        ImageResponse imageResponse = imageService.uploadImage(file, uploadRequest, username);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(imageResponse, "Image uploaded successfully"));
    }

    /**
     * Retrieves all images for a user.
     * 
     * @param userId the user ID
     * @return ResponseEntity containing list of user's images
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user images", description = "Retrieve all images for a specific user")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getUserImages(@PathVariable Long userId) {
        log.debug("Retrieving images for user ID: {}", userId);
        
        // Get username from userId
        String username = userService.getUserById(userId).getUsername();
        List<ImageResponse> images = imageService.getUserImages(username);
        
        return ResponseEntity.ok(ApiResponse.success(images, "Images retrieved successfully"));
    }

    /**
     * Retrieves a specific image by ID.
     * 
     * @param imageId the image ID
     * @param username the username of the image owner
     * @return ResponseEntity containing the image response
     */
    @GetMapping("/{imageId}")
    @Operation(summary = "Get image by ID", description = "Retrieve a specific image by its ID")
    public ResponseEntity<ApiResponse<ImageResponse>> getImageById(
            @PathVariable Long imageId,
            @RequestParam String username) {
        log.debug("Retrieving image by ID: {} for user: {}", imageId, username);
        
        ImageResponse imageResponse = imageService.getImageById(imageId, username);
        
        return ResponseEntity.ok(ApiResponse.success(imageResponse, "Image retrieved successfully"));
    }

    /**
     * Updates image metadata.
     * 
     * @param imageId the image ID
     * @param username the username of the image owner
     * @param updateRequest the image update request
     * @return ResponseEntity containing the updated image response
     */
    @PutMapping("/{imageId}")
    @Operation(summary = "Update image", description = "Update image metadata")
    public ResponseEntity<ApiResponse<ImageResponse>> updateImage(
            @PathVariable Long imageId,
            @RequestParam String username,
            @Valid @RequestBody ImageUploadRequest updateRequest) {
        log.info("Updating image with ID: {} for user: {}", imageId, username);
        
        // Note: This method doesn't exist in the current ImageService interface
        // You may need to add it or handle updates differently
        throw new UnsupportedOperationException("Image update not yet implemented");
    }

    /**
     * Deletes an image.
     * 
     * @param imageId the image ID to delete
     * @param username the username of the image owner
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete image", description = "Delete an image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long imageId,
            @RequestParam String username) {
        log.info("Deleting image with ID: {} for user: {}", imageId, username);
        
        imageService.deleteImage(imageId, username);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Image deleted successfully"));
    }
}