package com.synchrony.userservice.dto;

import com.synchrony.userservice.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for image response.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {

    /**
     * Image ID.
     */
    private Long id;

    /**
     * Image name.
     */
    private String imageName;

    /**
     * Original filename.
     */
    private String originalFilename;

    /**
     * Imgur image ID.
     */
    private String imgurId;

    /**
     * Imgur URL.
     */
    private String imgurUrl;

    /**
     * Dropbox path.
     */
    private String dropboxPath;

    /**
     * Image title.
     */
    private String title;

    /**
     * Image description.
     */
    private String description;

    /**
     * File size in bytes.
     */
    private Long fileSize;

    /**
     * MIME type.
     */
    private String mimeType;

    /**
     * Image width.
     */
    private Integer width;

    /**
     * Image height.
     */
    private Integer height;

    /**
     * Image status.
     */
    private Image.ImageStatus status;

    /**
     * View count.
     */
    private Long viewCount;

    /**
     * Tags.
     */
    private String tags;

    /**
     * Creation timestamp.
     */
    private LocalDateTime createdAt;

    /**
     * Update timestamp.
     */
    private LocalDateTime updatedAt;
}