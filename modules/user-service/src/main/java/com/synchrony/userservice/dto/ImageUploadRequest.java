package com.synchrony.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for image upload request.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {

    /**
     * Image title.
     */
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    /**
     * Image description.
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Image tags (comma-separated).
     */
    @Size(max = 200, message = "Tags must not exceed 200 characters")
    private String tags;
}