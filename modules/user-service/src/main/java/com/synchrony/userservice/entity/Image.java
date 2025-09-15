package com.synchrony.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Image entity representing an image in the system.
 * Enhanced with JPA auditing, optimistic locking, and comprehensive metadata.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "images", 
       indexes = {
           @Index(name = "idx_imgur_id", columnList = "imgur_id"),
           @Index(name = "idx_dropbox_path", columnList = "dropbox_path"),
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_image_name", columnList = "image_name"),
           @Index(name = "idx_mime_type", columnList = "mime_type"),
           @Index(name = "idx_file_size", columnList = "file_size")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    /**
     * Unique identifier for the image.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Name of the image file.
     */
    @Column(name = "image_name", nullable = false)
    private String imageName;

    /**
     * Original filename of the uploaded image.
     */
    @Column(name = "original_filename")
    private String originalFilename;

    /**
     * Imgur image ID.
     */
    @Column(name = "imgur_id", length = 100)
    private String imgurId;

    /**
     * Imgur delete hash for deleting the image.
     */
    @Column(name = "imgur_delete_hash", length = 100)
    private String imgurDeleteHash;

    /**
     * Direct URL to the image on Imgur.
     */
    @Column(name = "imgur_url", length = 500)
    private String imgurUrl;

    /**
     * Dropbox file path.
     */
    @Column(name = "dropbox_path", length = 500)
    private String dropboxPath;

    /**
     * Image title.
     */
    @Column(name = "title")
    private String title;

    /**
     * Image description.
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Image file size in bytes.
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Image MIME type.
     */
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * Image width in pixels.
     */
    @Column(name = "width")
    private Integer width;

    /**
     * Image height in pixels.
     */
    @Column(name = "height")
    private Integer height;

    /**
     * Image upload status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ImageStatus status = ImageStatus.ACTIVE;

    /**
     * Number of views/downloads.
     */
    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    /**
     * Tags associated with the image (comma-separated).
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * Version field for optimistic locking.
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Timestamp when the image was uploaded.
     */
    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the image was last updated.
     */
    @LastModifiedDate
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User who created this record (for auditing).
     */
    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    /**
     * User who last modified this record (for auditing).
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * User who owns this image.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_image_user"))
    private User user;

    /**
     * Enum for image status.
     */
    public enum ImageStatus {
        ACTIVE,
        DELETED,
        ARCHIVED,
        PROCESSING
    }

    /**
     * Gets the file size in a human-readable format.
     * 
     * @return formatted file size
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "Unknown";
        }
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    /**
     * Gets the image dimensions as a string.
     * 
     * @return dimensions string (e.g., "1920x1080")
     */
    public String getDimensions() {
        if (width != null && height != null) {
            return width + "x" + height;
        }
        return "Unknown";
    }

    /**
     * Checks if the image is active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return status == ImageStatus.ACTIVE;
    }

    /**
     * Increments the view count.
     */
    public void incrementViewCount() {
        if (viewCount == null) {
            viewCount = 0L;
        }
        viewCount++;
    }

    /**
     * Marks the image as deleted.
     */
    public void markAsDeleted() {
        this.status = ImageStatus.DELETED;
    }

    /**
     * Pre-persist callback to set default values.
     */
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ImageStatus.ACTIVE;
        }
        if (viewCount == null) {
            viewCount = 0L;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Pre-update callback to update timestamps.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}