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

    private final String imageDirectory = "uploads/images/";

    public String saveImage(MultipartFile file) throws IOException {
        // Verzeichnis erstellen, falls es nicht existiert
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Pfad und Dateiname erstellen und speichern
        String filePath = imageDirectory + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        return filePath;
    }

    public byte[] loadImage(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }
}