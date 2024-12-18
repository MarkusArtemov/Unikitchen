package com.dreamteam.unikitchen.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    // Saves the uploaded image and returns the file path
    public String saveImage(MultipartFile file) throws IOException {
        String imageDirectory = "uploads/images/";
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = imageDirectory + System.currentTimeMillis() + "_" + sanitizeFileName(file.getOriginalFilename());
        Path path = Paths.get(filePath);

        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new IOException("Failed to save image: " + e.getMessage(), e);
        }

        return filePath;
    }

    // Loads an image from the file system
    public byte[] loadImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }

        Path path = Paths.get(imagePath);

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IOException("Failed to load image: " + e.getMessage(), e);
        }
    }

    // Deletes an image by its path
    public boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(imagePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Failed to delete image: " + e.getMessage());
            return false;
        }
    }

    public String sanitizeFileName(String originalFileName) {
        if (originalFileName == null) {
            return "default.png";
        }
        return originalFileName.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }
}
