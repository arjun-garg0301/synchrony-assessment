package com.synchrony.userservice.repository;

import com.synchrony.userservice.entity.Image;
import com.synchrony.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Image entity operations.
 * Enhanced with pagination, sorting, and advanced query methods.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Finds all images belonging to a specific user.
     * 
     * @param user the user whose images to retrieve
     * @return List of images belonging to the user
     */
    List<Image> findByUser(User user);

    /**
     * Finds all images belonging to a specific user with pagination.
     * 
     * @param user the user whose images to retrieve
     * @param pageable pagination information
     * @return Page of images belonging to the user
     */
    Page<Image> findByUser(User user, Pageable pageable);

    /**
     * Finds all images belonging to a specific user ID.
     * 
     * @param userId the user ID whose images to retrieve
     * @return List of images belonging to the user
     */
    List<Image> findByUserId(Long userId);

    /**
     * Finds an image by its Imgur ID.
     * 
     * @param imgurId the Imgur ID to search for
     * @return Optional containing the image if found
     */
    Optional<Image> findByImgurId(String imgurId);

    /**
     * Finds an image by its ID and user.
     * 
     * @param id the image ID
     * @param user the user who owns the image
     * @return Optional containing the image if found
     */
    Optional<Image> findByIdAndUser(Long id, User user);

    /**
     * Finds an image by its Imgur ID and user.
     * 
     * @param imgurId the Imgur ID
     * @param user the user who owns the image
     * @return Optional containing the image if found
     */
    Optional<Image> findByImgurIdAndUser(String imgurId, User user);

    /**
     * Counts the number of images for a specific user.
     * 
     * @param user the user whose images to count
     * @return number of images belonging to the user
     */
    long countByUser(User user);

    /**
     * Finds images by user and image name containing the specified text.
     * 
     * @param user the user who owns the images
     * @param imageName the image name to search for
     * @return List of matching images
     */
    @Query("SELECT i FROM Image i WHERE i.user = :user AND i.imageName LIKE %:imageName%")
    List<Image> findByUserAndImageNameContaining(@Param("user") User user, @Param("imageName") String imageName);

    /**
     * Checks if an image exists by Imgur ID.
     * 
     * @param imgurId the Imgur ID to check
     * @return true if image exists, false otherwise
     */
    boolean existsByImgurId(String imgurId);

    /**
     * Finds images by user and status.
     * 
     * @param user the user who owns the images
     * @param status the image status
     * @return List of images with the specified status
     */
    List<Image> findByUserAndStatus(User user, Image.ImageStatus status);

    /**
     * Finds images created after a specific date.
     * 
     * @param user the user who owns the images
     * @param date the date to search after
     * @return List of images created after the specified date
     */
    @Query("SELECT i FROM Image i WHERE i.user = :user AND i.createdAt > :date")
    List<Image> findByUserAndCreatedAfter(@Param("user") User user, @Param("date") LocalDateTime date);

    /**
     * Finds images by MIME type.
     * 
     * @param user the user who owns the images
     * @param mimeType the MIME type to search for
     * @return List of images with the specified MIME type
     */
    List<Image> findByUserAndMimeType(User user, String mimeType);

    /**
     * Finds images larger than specified file size.
     * 
     * @param user the user who owns the images
     * @param fileSize the minimum file size
     * @return List of images larger than the specified size
     */
    @Query("SELECT i FROM Image i WHERE i.user = :user AND i.fileSize > :fileSize")
    List<Image> findByUserAndFileSizeGreaterThan(@Param("user") User user, @Param("fileSize") Long fileSize);
}