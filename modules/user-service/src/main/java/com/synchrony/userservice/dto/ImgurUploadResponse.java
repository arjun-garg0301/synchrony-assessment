package com.synchrony.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Imgur API upload response.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImgurUploadResponse {

    /**
     * Indicates if the upload was successful.
     */
    private boolean success;

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Upload data.
     */
    private ImgurImageData data;

    /**
     * Inner class representing Imgur image data.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImgurImageData {

        /**
         * Imgur image ID.
         */
        private String id;

        /**
         * Image title.
         */
        private String title;

        /**
         * Image description.
         */
        private String description;

        /**
         * Image datetime (timestamp).
         */
        private Long datetime;

        /**
         * Image type (MIME type).
         */
        private String type;

        /**
         * Indicates if the image is animated.
         */
        private boolean animated;

        /**
         * Image width in pixels.
         */
        private Integer width;

        /**
         * Image height in pixels.
         */
        private Integer height;

        /**
         * Image file size in bytes.
         */
        private Long size;

        /**
         * Number of views.
         */
        private Long views;

        /**
         * Image bandwidth usage.
         */
        private Long bandwidth;

        /**
         * Vote score.
         */
        private Integer vote;

        /**
         * Indicates if the image is favorited.
         */
        private boolean favorite;

        /**
         * Indicates if the image is NSFW.
         */
        private boolean nsfw;

        /**
         * Image section.
         */
        private String section;

        /**
         * Account URL of the uploader.
         */
        @JsonProperty("account_url")
        private String accountUrl;

        /**
         * Account ID of the uploader.
         */
        @JsonProperty("account_id")
        private Long accountId;

        /**
         * Indicates if the image is ad.
         */
        @JsonProperty("is_ad")
        private boolean isAd;

        /**
         * Indicates if the image is in the most viral gallery.
         */
        @JsonProperty("in_most_viral")
        private boolean inMostViral;

        /**
         * Indicates if the image has sound.
         */
        @JsonProperty("has_sound")
        private boolean hasSound;

        /**
         * Tags associated with the image.
         */
        private String[] tags;

        /**
         * Ad type.
         */
        @JsonProperty("ad_type")
        private Integer adType;

        /**
         * Ad URL.
         */
        @JsonProperty("ad_url")
        private String adUrl;

        /**
         * Edited timestamp.
         */
        private String edited;

        /**
         * Indicates if the image is in the gallery.
         */
        @JsonProperty("in_gallery")
        private boolean inGallery;

        /**
         * Delete hash for deleting the image.
         */
        @JsonProperty("deletehash")
        private String deleteHash;

        /**
         * Image name.
         */
        private String name;

        /**
         * Direct link to the image.
         */
        private String link;
    }
}