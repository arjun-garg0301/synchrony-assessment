package com.synchrony.common.util;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.synchrony.common.scheduler.DropboxTokenScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class DropboxService {

    private static final String BASE_FOLDER = "/synchrony-assessment";
    private static DbxClientV2 client;

    @Autowired
    public DropboxService(DropboxTokenScheduler tokenScheduler) {
        String accessToken = DropboxTokenScheduler.getCurrentAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("Access token is not available at startup.");
        } else {
            updateDropboxClient(accessToken);
        }
    }

    /**
     * Uploads an image file for a specific user.
     * 
     * @param userId the user ID
     * @param file the image file input stream
     * @param fileName the name of the file
     * @return the Dropbox file path
     * @throws DbxException if Dropbox API error occurs
     * @throws IOException if I/O error occurs
     */
    public static String uploadImage(Long userId, InputStream file, String fileName) throws DbxException, IOException {
        // Ensure that the client is initialized
        if (client == null) {
            refreshClient();
        }

        String userFolderPath = BASE_FOLDER + "/user-" + userId + "/images";
        String filePath = userFolderPath + "/" + fileName;

        // Check if the folder exists, if not create it
        if (!folderExists(userFolderPath)) {
            createFolderRecursively(userFolderPath);
        }

        // Upload the file
        try (InputStream in = file) {
            FileMetadata metadata = client.files().uploadBuilder(filePath).uploadAndFinish(in);
            log.info("Uploaded image: {} for user: {}", fileName, userId);
            return metadata.getPathDisplay();
        }
    }

    /**
     * Downloads an image file from Dropbox.
     * 
     * @param filePath the Dropbox file path
     * @return byte array of the file content
     * @throws DbxException if Dropbox API error occurs
     * @throws IOException if I/O error occurs
     */
    public static byte[] downloadImage(String filePath) throws DbxException, IOException {
        if (client == null) {
            refreshClient();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        client.files().downloadBuilder(filePath).download(outputStream);
        log.info("Downloaded image from path: {}", filePath);
        return outputStream.toByteArray();
    }

    /**
     * Deletes an image file from Dropbox.
     * 
     * @param filePath the Dropbox file path
     * @throws DbxException if Dropbox API error occurs
     */
    public static void deleteImage(String filePath) throws DbxException {
        if (client == null) {
            refreshClient();
        }

        client.files().deleteV2(filePath);
        log.info("Deleted image from path: {}", filePath);
    }

    /**
     * Lists all images for a specific user.
     * 
     * @param userId the user ID
     * @return list of file metadata
     * @throws DbxException if Dropbox API error occurs
     */
    public static List<Metadata> listUserImages(Long userId) throws DbxException {
        if (client == null) {
            refreshClient();
        }

        String userFolderPath = BASE_FOLDER + "/user-" + userId + "/images";
        
        try {
            return client.files().listFolder(userFolderPath).getEntries();
        } catch (DbxException e) {
            if (e.getMessage().contains("path/not_found")) {
                log.info("No images folder found for user: {}", userId);
                return List.of();
            }
            throw e;
        }
    }

    /**
     * Downloads all images for a user as a ZIP file.
     * 
     * @param userId the user ID
     * @return byte array of the ZIP file
     */
    public static byte[] downloadUserImagesAsZip(Long userId) {
        String userFolderPath = BASE_FOLDER + "/user-" + userId + "/images";
        return downloadFolderAsZip(userFolderPath);
    }

    public static void uploadFiles(String email, List<InputStream> files, List<String> fileNames, String category) throws DbxException, IOException {
        // ensure that the client is initialized
        if (client == null) {
            refreshClient();
        }

        String folderPath = "/" + email + "/" + category;

        // Check if the folder exists, if not create it
        if (!folderExists(folderPath)) {
            createFolderRecursively(folderPath);
        }

        // Upload each file to the folder
        for (int i = 0; i < files.size(); i++) {
            try (InputStream in = files.get(i)) {
                client.files().uploadBuilder(folderPath + "/" + fileNames.get(i)).uploadAndFinish(in);
                log.info("Uploaded file: {}", fileNames.get(i));
            }
        }
    }

    private static boolean folderExists(String folderPath) {
        try {
            Metadata metadata = client.files().getMetadata(folderPath);
            return metadata instanceof FolderMetadata;
        } catch (DbxException e) {
            return false;
        }
    }

    private static void createFolderRecursively(String folderPath) throws DbxException {
        String[] pathParts = folderPath.split("/");
        StringBuilder currentPath = new StringBuilder();
        
        for (String part : pathParts) {
            if (part.isEmpty()) continue;
            
            currentPath.append("/").append(part);
            String path = currentPath.toString();
            
            if (!folderExists(path)) {
                try {
                    client.files().createFolderV2(path);
                    log.info("Created folder: {}", path);
                } catch (DbxException e) {
                    if (!e.getMessage().contains("path/conflict")) {
                        throw e;
                    }
                }
            }
        }
    }

    public static byte[] downloadFile(String pathToFile) throws DbxException, IOException {
        if (client == null) {
            throw new IllegalStateException("Dropbox client is not initialized.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        client.files().downloadBuilder(pathToFile).download(outputStream);
        return outputStream.toByteArray();
    }

    private static void refreshClient() {
        try {
            String accessToken = DropboxTokenScheduler.getCurrentAccessToken();
            if (accessToken == null || accessToken.isEmpty()) {
                throw new IllegalStateException("Access token is not available.");
            }
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/synchrony-assessment").build();
            client = new DbxClientV2(config, accessToken);
            log.info("Dropbox client re-initialized with refreshed access token.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to refresh Dropbox client: " + e.getMessage(), e);
        }
    }

    public static byte[] downloadFolderAsZip(String folderPath) {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipOutputStream)) {
            List<Metadata> files = client.files().listFolder(folderPath).getEntries();

            for (Metadata metadata : files) {
                if (metadata instanceof FileMetadata) {
                    String filePath = folderPath + "/" + metadata.getName();
                    byte[] fileData = downloadFile(filePath);
                    ZipEntry zipEntry = new ZipEntry(metadata.getName());
                    zos.putNextEntry(zipEntry);
                    zos.write(fileData);
                    zos.closeEntry();
                }
            }
        } catch (IOException | DbxException e) {
            log.error("Error getting the zipped files from Dropbox, error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return zipOutputStream.toByteArray();
    }

    public void updateDropboxClient(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Missing access token.");
        }
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/synchrony-assessment").build();
        client = new DbxClientV2(config, accessToken);
        log.info("Dropbox client initialized with new access token.");
    }

    public DbxClientV2 getClient() {
        if (client == null) {
            throw new IllegalStateException("Dropbox client is not initialized.");
        }
        return client;
    }
}