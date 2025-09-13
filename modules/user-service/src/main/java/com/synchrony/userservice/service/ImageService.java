package com.synchrony.userservice.service;

import com.synchrony.userservice.dto.ImageResponse;
import com.synchrony.userservice.dto.ImageUploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for image-related operations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImageService {

    /**
     * Uploads an image for a specific user.
     * 
     * @param file the image file to upload
     * @param uploadRequest the upload request containing metadata
     * @param username the username of the user uploading the image
     * @return ImageResponse containing the uploaded image information
     */
    ImageResponse uploadImage(MultipartFile file, ImageUploadRequest uploadRequest, String username);

    /**
     * Retrieves all images for a specific user.
     * 
     * @param username the username whose images to retrieve
     * @return List of ImageResponse containing user's images
     */
    List<ImageResponse> getUserImages(String username);

    /**
     * Retrieves a specific image by ID for a user.
     * 
     * @param imageId the image ID to retrieve
     * @param username the username of the image owner
     * @return ImageResponse containing the image information
     */
    ImageResponse getImageById(Long imageId, String username);

    /**
     * Retrieves a specific image by Imgur ID for a user.
     * 
     * @param imgurId the Imgur ID to retrieve
     * @param username the username of the image owner
     * @return ImageResponse containing the image information
     */
    ImageResponse getImageByImgurId(String imgurId, String username);

    /**
     * Deletes an image by ID for a user.
     * 
     * @param imageId the image ID to delete
     * @param username the username of the image owner
     */
    void deleteImage(Long imageId, String username);

    /**
     * Deletes an image by Imgur ID for a user.
     * 
     * @param imgurId the Imgur ID to delete
     * @param username the username of the image owner
     */
    void deleteImageByImgurId(String imgurId, String username);

    /**
     * Searches images by name for a specific user.
     * 
     * @param imageName the image name to search for
     * @param username the username whose images to search
     * @return List of ImageResponse containing matching images
     */
    List<ImageResponse> searchImagesByName(String imageName, String username);

    /**
     * Gets the count of images for a specific user.
     * 
     * @param username the username whose images to count
     * @return number of images belonging to the user
     */
    long getUserImageCount(String username);
}