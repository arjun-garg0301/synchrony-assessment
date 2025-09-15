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
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing a user in the system.
 * Enhanced with JPA auditing, optimistic locking, and comprehensive indexing.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "users", 
       indexes = {
           @Index(name = "idx_username", columnList = "username"),
           @Index(name = "idx_email", columnList = "email"),
           @Index(name = "idx_active", columnList = "is_active"),
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_email", columnNames = "email")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Unique username for the user.
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * User's email address.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * User's encrypted password.
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * User's first name.
     */
    @Column(name = "first_name", length = 50)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /**
     * User's phone number.
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Indicates if the user account is active.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Version field for optimistic locking.
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Timestamp when the user was created.
     */
    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user was last updated.
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
     * List of images associated with the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    /**
     * Adds an image to the user's image list.
     * 
     * @param image the image to add
     */
    public void addImage(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
        image.setUser(this);
    }

    /**
     * Removes an image from the user's image list.
     * 
     * @param image the image to remove
     */
    public void removeImage(Image image) {
        if (images != null) {
            images.remove(image);
            image.setUser(null);
        }
    }

    /**
     * Gets the total number of images for this user.
     * 
     * @return number of images
     */
    public int getImageCount() {
        return images != null ? images.size() : 0;
    }

    /**
     * Checks if the user has any images.
     * 
     * @return true if user has images, false otherwise
     */
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    /**
     * Gets the user's full name.
     * 
     * @return full name or username if names are not available
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }

    /**
     * Checks if the user account is active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Pre-persist callback to set default values.
     */
    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = true;
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