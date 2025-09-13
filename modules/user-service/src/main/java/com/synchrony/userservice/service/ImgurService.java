package com.synchrony.userservice.service;

import com.synchrony.userservice.dto.ImgurUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for Imgur API integration.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImgurService {

    /**
     * Uploads an image to Imgur.
     * 
     * @param file the image file to upload
     * @param title the image title (optional)
     * @param description the image description (optional)
     * @return ImgurUploadResponse containing upload details
     */
    ImgurUploadResponse uploadImage(MultipartFile file, String title, String description);

    /**
     * Deletes an image from Imgur.
     * 
     * @param deleteHash the delete hash of the image to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteImage(String deleteHash);

    /**
     * Retrieves image information from Imgur.
     * 
     * @param imageId the Imgur image ID
     * @return ImgurUploadResponse containing image details
     */
    ImgurUploadResponse getImageInfo(String imageId);
}