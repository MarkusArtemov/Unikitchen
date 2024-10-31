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

    // Verzeichnis für das Speichern von Bildern
    private final String imageDirectory = "uploads/images/";

    // Speichert ein Bild und gibt den Pfad zurück
    public String saveImage(MultipartFile file) throws IOException {
        // Erstelle das Verzeichnis, falls es nicht existiert
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generiere einen Dateipfad für das neue Bild
        String filePath = imageDirectory + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        return filePath;
    }

    // Lädt ein Bild basierend auf seinem Dateipfad
    public byte[] loadImage(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }

    // Löscht ein Bild basierend auf seinem Dateipfad
    public boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(imagePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            // Loggt den Fehler, aber lässt das Hochladen fortfahren, auch wenn das Löschen fehlschlägt
            System.err.println("Fehler beim Löschen des Bildes: " + e.getMessage());
            return false;
        }
    }
}