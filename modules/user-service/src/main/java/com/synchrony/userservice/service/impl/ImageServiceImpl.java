package com.synchrony.userservice.service.impl;

import com.synchrony.common.exception.ResourceNotFoundException;
import com.synchrony.common.exception.BusinessException;
import com.synchrony.userservice.dto.ImageResponse;
import com.synchrony.userservice.dto.ImageUploadRequest;
import com.synchrony.userservice.dto.ImgurUploadResponse;
import com.synchrony.userservice.entity.Image;
import com.synchrony.userservice.entity.User;
import com.synchrony.userservice.repository.ImageRepository;
import com.synchrony.userservice.service.ImageService;
import com.synchrony.userservice.service.ImgurService;
import com.synchrony.userservice.service.KafkaProducerService;
import com.synchrony.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ImageService interface.
 * Enhanced with proper error handling and business logic.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserService userService;
    private final ImgurService imgurService;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository,
                           UserService userService,
                           ImgurService imgurService,
                           KafkaProducerService kafkaProducerService) {
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.imgurService = imgurService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public ImageResponse uploadImage(MultipartFile file, ImageUploadRequest uploadRequest, String username) {
        log.info("Uploading image for user: {}, filename: {}", username, file.getOriginalFilename());
        
        try {
            // Find user
            User user = userService.findUserByUsername(username);
            
            Image image = null;
            
            // Try Imgur first
            try {
                log.info("Attempting to upload to Imgur for user: {}", username);
                ImgurUploadResponse imgurResponse = imgurService.uploadImage(
                        file, 
                        uploadRequest.getTitle(), 
                        uploadRequest.getDescription()
                );
                
                if (imgurResponse != null && imgurResponse.isSuccess() && imgurResponse.getData() != null) {
                    ImgurUploadResponse.ImgurImageData imgurData = imgurResponse.getData();
                    
                    // Create image entity with Imgur data
                    image = Image.builder()
                            .imageName(generateImageName(file.getOriginalFilename(), imgurData.getId()))
                            .originalFilename(file.getOriginalFilename())
                            .imgurId(imgurData.getId())
                            .imgurDeleteHash(imgurData.getDeleteHash())
                            .imgurUrl(imgurData.getLink())
                            .title(uploadRequest.getTitle())
                            .description(uploadRequest.getDescription())
                            .fileSize(imgurData.getSize())
                            .mimeType(imgurData.getType())
                            .width(imgurData.getWidth())
                            .height(imgurData.getHeight())
                            .status(Image.ImageStatus.ACTIVE)
                            .user(user)
                            .build();
                    
                    log.info("Image uploaded successfully to Imgur for user: {}", username);
                } else {
                    log.warn("Imgur upload failed, response was null or unsuccessful");
                    throw new BusinessException("Imgur upload failed", "IMGUR_UPLOAD_FAILED");
                }
                
            } catch (Exception imgurException) {
                log.warn("Imgur upload failed for user {}: {}. Falling back to Dropbox.", username, imgurException.getMessage());
                
                // Fallback to Dropbox
                try {
                    log.info("Attempting to upload to Dropbox for user: {}", username);
                    
                    // Generate unique filename for Dropbox
                    String originalFilename = file.getOriginalFilename();
                    String fileExtension = getFileExtension(originalFilename);
                    String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
                    
                    // Upload to Dropbox using the common service
                    String dropboxPath = com.synchrony.common.util.DropboxService.uploadImage(
                            user.getId(), 
                            file.getInputStream(), 
                            uniqueFilename
                    );
                    
                    // Create image entity with Dropbox data
                    image = Image.builder()
                            .imageName(uniqueFilename)
                            .originalFilename(originalFilename)
                            .dropboxPath(dropboxPath)
                            .title(uploadRequest.getTitle())
                            .description(uploadRequest.getDescription())
                            .fileSize(file.getSize())
                            .mimeType(file.getContentType())
                            .status(Image.ImageStatus.ACTIVE)
                            .user(user)
                            .build();
                    
                    log.info("Image uploaded successfully to Dropbox for user: {}", username);
                    
                } catch (Exception dropboxException) {
                    log.error("Both Imgur and Dropbox uploads failed for user {}: Imgur={}, Dropbox={}", 
                            username, imgurException.getMessage(), dropboxException.getMessage());
                    throw new BusinessException("Failed to upload image to both Imgur and Dropbox", "UPLOAD_FAILED");
                }
            }
            
            if (image == null) {
                throw new BusinessException("Failed to create image entity", "IMAGE_CREATION_FAILED");
            }
            
            Image savedImage = imageRepository.save(image);
            log.info("Image saved successfully: imageId={}, imgurId={}, dropboxPath={}", 
                    savedImage.getId(), savedImage.getImgurId(), savedImage.getDropboxPath());
            
            // Send Kafka message asynchronously
            sendImageEventAsync(username, savedImage.getImageName(), savedImage.getImgurId() != null ? "IMGUR" : "DROPBOX");
            
            return convertToImageResponse(savedImage);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading image for user '{}': {}", username, e.getMessage(), e);
            throw new BusinessException("Failed to upload image: " + e.getMessage(), "IMAGE_UPLOAD_ERROR", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "userImages", key = "'user:' + #username")
    public List<ImageResponse> getUserImages(String username) {
        log.debug("Retrieving images for user: {}", username);
        
        try {
            User user = userService.findUserByUsername(username);
            List<Image> images = imageRepository.findByUserAndStatus(user, Image.ImageStatus.ACTIVE);
            
            return images.stream()
                    .map(this::convertToImageResponse)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error retrieving images for user '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user images", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "imageDetails", key = "'image:' + #imageId + ':user:' + #username")
    public ImageResponse getImageById(Long imageId, String username) {
        log.debug("Retrieving image by ID: {} for user: {}", imageId, username);
        
        try {
            User user = userService.findUserByUsername(username);
            Image image = imageRepository.findByIdAndUser(imageId, user)
                    .orElseThrow(() -> {
                        log.warn("Image not found: imageId={}, username={}", imageId, username);
                        return ResourceNotFoundException.forResource("Image", imageId);
                    });
            
            // Increment view count (this will invalidate cache, but that's expected for view tracking)
            image.incrementViewCount();
            imageRepository.save(image);
            
            return convertToImageResponse(image);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving image by ID '{}' for user '{}': {}", imageId, username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve image", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "imageDetails", key = "'imgur:' + #imgurId + ':user:' + #username")
    public ImageResponse getImageByImgurId(String imgurId, String username) {
        log.debug("Retrieving image by Imgur ID: {} for user: {}", imgurId, username);
        
        try {
            User user = userService.findUserByUsername(username);
            Image image = imageRepository.findByImgurIdAndUser(imgurId, user)
                    .orElseThrow(() -> {
                        log.warn("Image not found: imgurId={}, username={}", imgurId, username);
                        return ResourceNotFoundException.forResourceField("Image", "imgurId", imgurId);
                    });
            
            return convertToImageResponse(image);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving image by Imgur ID '{}' for user '{}': {}", imgurId, username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve image", e);
        }
    }

    @Override
    @org.springframework.cache.annotation.CacheEvict(value = {"userImages", "imageDetails"}, allEntries = true)
    public void deleteImage(Long imageId, String username) {
        log.info("Deleting image: imageId={}, username={}", imageId, username);
        
        try {
            User user = userService.findUserByUsername(username);
            Image image = imageRepository.findByIdAndUser(imageId, user)
                    .orElseThrow(() -> {
                        log.warn("Image not found for deletion: imageId={}, username={}", imageId, username);
                        return ResourceNotFoundException.forResource("Image", imageId);
                    });
            
            // Delete from Imgur
            boolean imgurDeleted = imgurService.deleteImage(image.getImgurDeleteHash());
            if (!imgurDeleted) {
                log.warn("Failed to delete image from Imgur: imgurId={}", image.getImgurId());
            }
            
            // Mark as deleted instead of hard delete
            image.markAsDeleted();
            imageRepository.save(image);
            
            log.info("Image deleted successfully: imageId={}, imgurId={}", imageId, image.getImgurId());
            
            // Send Kafka message asynchronously
            sendImageEventAsync(username, image.getImageName(), "DELETED");
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting image '{}' for user '{}': {}", imageId, username, e.getMessage(), e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    @Override
    public void deleteImageByImgurId(String imgurId, String username) {
        log.info("Deleting image by Imgur ID: imgurId={}, username={}", imgurId, username);
        
        try {
            User user = userService.findUserByUsername(username);
            Image image = imageRepository.findByImgurIdAndUser(imgurId, user)
                    .orElseThrow(() -> {
                        log.warn("Image not found for deletion: imgurId={}, username={}", imgurId, username);
                        return ResourceNotFoundException.forResourceField("Image", "imgurId", imgurId);
                    });
            
            deleteImage(image.getId(), username);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting image by Imgur ID '{}' for user '{}': {}", imgurId, username, e.getMessage(), e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageResponse> searchImagesByName(String imageName, String username) {
        log.debug("Searching images by name: imageName={}, username={}", imageName, username);
        
        try {
            User user = userService.findUserByUsername(username);
            List<Image> images = imageRepository.findByUserAndImageNameContaining(user, imageName);
            
            return images.stream()
                    .filter(image -> image.getStatus() == Image.ImageStatus.ACTIVE)
                    .map(this::convertToImageResponse)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error searching images by name '{}' for user '{}': {}", imageName, username, e.getMessage(), e);
            throw new RuntimeException("Failed to search images", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserImageCount(String username) {
        log.debug("Getting image count for user: {}", username);
        
        try {
            User user = userService.findUserByUsername(username);
            return imageRepository.findByUserAndStatus(user, Image.ImageStatus.ACTIVE).size();
            
        } catch (Exception e) {
            log.error("Error getting image count for user '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to get image count", e);
        }
    }

    /**
     * Generates a unique image name.
     * 
     * @param originalFilename the original filename
     * @param imgurId the Imgur ID
     * @return generated image name
     */
    private String generateImageName(String originalFilename, String imgurId) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return "image_" + imgurId;
        }
        
        String nameWithoutExtension = originalFilename;
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            nameWithoutExtension = originalFilename.substring(0, lastDotIndex);
        }
        
        return nameWithoutExtension + "_" + imgurId;
    }

    /**
     * Converts Image entity to ImageResponse DTO.
     * 
     * @param image the Image entity to convert
     * @return ImageResponse DTO
     */
    private ImageResponse convertToImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .imageName(image.getImageName())
                .originalFilename(image.getOriginalFilename())
                .imgurId(image.getImgurId())
                .imgurUrl(image.getImgurUrl())
                .dropboxPath(image.getDropboxPath())
                .title(image.getTitle())
                .description(image.getDescription())
                .fileSize(image.getFileSize())
                .mimeType(image.getMimeType())
                .width(image.getWidth())
                .height(image.getHeight())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
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

    /**
     * Sends image event asynchronously to improve performance.
     * 
     * @param username the username
     * @param imageName the image name
     * @param provider the upload provider (IMGUR/DROPBOX/DELETED)
     */
    @org.springframework.scheduling.annotation.Async("kafkaExecutor")
    private void sendImageEventAsync(String username, String imageName, String provider) {
        try {
            kafkaProducerService.sendImageEvent(username, imageName, "IMAGE_" + provider);
            log.debug("Kafka event sent asynchronously for user: {}, image: {}", username, imageName);
        } catch (Exception e) {
            log.debug("Failed to send Kafka message for image event: {}", e.getMessage());
            // Don't log as warning since Kafka might be disabled
        }
    }
}