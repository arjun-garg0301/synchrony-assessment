package com.synchrony.userservice.controller;

import com.synchrony.common.dto.ApiResponse;
import com.synchrony.userservice.dto.ImageResponse;
import com.synchrony.common.util.DropboxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for Dropbox-specific image operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/dropbox")
@Tag(name = "Dropbox Management", description = "Dropbox-specific image management APIs")
public class DropboxController {

    /**
     * Uploads an image directly to Dropbox for a user.
     * 
     * @param userId the user ID
     * @param file the image file to upload
     * @param title the image title (optional)
     * @param description the image description (optional)
     * @return ResponseEntity containing the uploaded image response
     */
    @PostMapping(value = "/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image to Dropbox", description = "Upload an image directly to Dropbox for a user")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadImageToDropbox(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        log.info("Direct Dropbox upload request for user ID: {}", userId);
        
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
            
            // Upload to Dropbox
            String dropboxPath = DropboxService.uploadImage(userId, file.getInputStream(), uniqueFilename);
            
            // Create response
            ImageResponse imageResponse = ImageResponse.builder()
                    .imageName(uniqueFilename)
                    .originalFilename(originalFilename)
                    .dropboxPath(dropboxPath)
                    .title(title)
                    .description(description)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .createdAt(java.time.LocalDateTime.now())
                    .updatedAt(java.time.LocalDateTime.now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(imageResponse, "Image uploaded successfully to Dropbox"));
                    
        } catch (Exception e) {
            log.error("Error uploading image to Dropbox for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload image to Dropbox: " + e.getMessage()));
        }
    }

    /**
     * Downloads an image from Dropbox.
     * 
     * @param dropboxPath the Dropbox file path (URL encoded)
     * @return ResponseEntity containing the image bytes
     */
    @GetMapping("/download")
    @Operation(summary = "Download image from Dropbox", description = "Download an image from Dropbox by path")
    public ResponseEntity<byte[]> downloadImageFromDropbox(@RequestParam String dropboxPath) {
        log.info("Downloading image from Dropbox: {}", dropboxPath);
        
        try {
            byte[] imageData = DropboxService.downloadImage(dropboxPath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", getFilenameFromPath(dropboxPath));
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageData);
                    
        } catch (Exception e) {
            log.error("Error downloading image from Dropbox {}: {}", dropboxPath, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lists all images for a user from Dropbox.
     * 
     * @param userId the user ID
     * @return ResponseEntity containing list of user's images from Dropbox
     */
    @GetMapping("/images/{userId}")
    @Operation(summary = "List user images from Dropbox", description = "Retrieve all images for a specific user from Dropbox")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> listUserImagesFromDropbox(@PathVariable Long userId) {
        log.debug("Listing Dropbox images for user ID: {}", userId);
        
        try {
            List<com.dropbox.core.v2.files.Metadata> files = DropboxService.listUserImages(userId);
            
            List<ImageResponse> images = files.stream()
                    .filter(metadata -> metadata instanceof com.dropbox.core.v2.files.FileMetadata)
                    .map(metadata -> {
                        com.dropbox.core.v2.files.FileMetadata fileMetadata = (com.dropbox.core.v2.files.FileMetadata) metadata;
                        return ImageResponse.builder()
                                .imageName(fileMetadata.getName())
                                .originalFilename(fileMetadata.getName())
                                .dropboxPath(fileMetadata.getPathDisplay())
                                .fileSize(fileMetadata.getSize())
                                .createdAt(fileMetadata.getClientModified() != null ? 
                                    fileMetadata.getClientModified().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                                    java.time.LocalDateTime.now())
                                .updatedAt(fileMetadata.getServerModified() != null ? 
                                    fileMetadata.getServerModified().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                                    java.time.LocalDateTime.now())
                                .build();
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(images, "Images retrieved successfully from Dropbox"));
            
        } catch (Exception e) {
            log.error("Error listing images from Dropbox for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to list images from Dropbox: " + e.getMessage()));
        }
    }

    /**
     * Downloads all images for a user as a ZIP file from Dropbox.
     * 
     * @param userId the user ID
     * @return ResponseEntity containing the ZIP file
     */
    @GetMapping("/download-zip/{userId}")
    @Operation(summary = "Download user images as ZIP", description = "Download all images for a user as a ZIP file from Dropbox")
    public ResponseEntity<byte[]> downloadUserImagesAsZip(@PathVariable Long userId) {
        log.info("Downloading ZIP file for user ID: {}", userId);
        
        try {
            byte[] zipData = DropboxService.downloadUserImagesAsZip(userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "user-" + userId + "-images.zip");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipData);
                    
        } catch (Exception e) {
            log.error("Error creating ZIP file for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes an image from Dropbox.
     * 
     * @param dropboxPath the Dropbox file path (URL encoded)
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/delete")
    @Operation(summary = "Delete image from Dropbox", description = "Delete an image from Dropbox by path")
    public ResponseEntity<ApiResponse<Void>> deleteImageFromDropbox(@RequestParam String dropboxPath) {
        log.info("Deleting image from Dropbox: {}", dropboxPath);
        
        try {
            DropboxService.deleteImage(dropboxPath);
            return ResponseEntity.ok(ApiResponse.success(null, "Image deleted successfully from Dropbox"));
            
        } catch (Exception e) {
            log.error("Error deleting image from Dropbox {}: {}", dropboxPath, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete image from Dropbox: " + e.getMessage()));
        }
    }

    /**
     * Extracts filename from Dropbox path.
     * 
     * @param path the Dropbox file path
     * @return filename
     */
    private String getFilenameFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return "download";
        }
        
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return path;
        }
        
        return path.substring(lastSlashIndex + 1);
    }

    /**
     * Extracts file extension from filename.
     * 
     * @param filename the filename
     * @return file extension with dot (e.g., ".jpg")
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex);
    }
}