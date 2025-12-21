package com.farhetna.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
    
    @Value("${app.storage.upload-dir}")
    private String uploadDir;
    
    @Value("${app.storage.image-dir}")
    private String imageDir;
    
    @Value("${app.storage.video-dir}")
    private String videoDir;
    
    public String storeFile(MultipartFile file, FileType fileType) {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Determine storage directory
            String storageDir;
switch (fileType) {
    case IMAGE:
        storageDir = imageDir;
        break;
    case VIDEO:
        storageDir = videoDir;
        break;
    default:
        storageDir = uploadDir;
        break;
}

            
            // Create directories if they don't exist
            Path targetLocation = Paths.get(storageDir).resolve(uniqueFilename);
            Files.createDirectories(targetLocation.getParent());
            
            // Copy file to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File stored successfully: {}", uniqueFilename);
            return "/uploads/" + fileType.name().toLowerCase() + "/" + uniqueFilename;
            
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    public void deleteFile(String fileUrl) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileUrl);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", fileUrl);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
        }
    }
    
    public enum FileType {
        IMAGE, VIDEO, DOCUMENT
    }
}