package com.dreamteam.unikitchen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageService();
    }

    @Test
    void testSaveImage_CreatesDirectoryAndSavesFile() throws IOException {
        String imageDirectory = "uploads/images/";
        String originalFileName = "test_image.jpg";
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(originalFileName);
        when(file.getBytes()).thenReturn("dummy content".getBytes());

        String savedFilePath = imageService.saveImage(file);

        assertTrue(savedFilePath.startsWith(imageDirectory));
        File savedFile = new File(savedFilePath);
        assertTrue(savedFile.exists());
        savedFile.delete();
        new File(imageDirectory).delete();
    }

    @Test
    void testSaveImage_ThrowsIOExceptionOnFailure() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.jpg");
        when(file.getBytes()).thenThrow(new IOException("Disk full"));

        IOException exception = assertThrows(IOException.class, () -> imageService.saveImage(file));
        assertTrue(exception.getMessage().contains("Failed to save image"));
    }

    @Test
    void testLoadImage_ReturnsFileContent() throws IOException {
        Path tempFile = Files.createTempFile("test_image", ".jpg");
        Files.writeString(tempFile, "dummy content");
        String imagePath = tempFile.toString();
        byte[] imageBytes = imageService.loadImage(imagePath);

        assertNotNull(imageBytes);
        assertEquals("dummy content", new String(imageBytes));

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testLoadImage_ThrowsExceptionForInvalidPath() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> imageService.loadImage(null));
        assertEquals("Image path cannot be null or empty", exception.getMessage());

        IOException ioException = assertThrows(IOException.class, () -> imageService.loadImage("nonexistent_file.jpg"));
        assertTrue(ioException.getMessage().contains("Failed to load image"));
    }

    @Test
    void testDeleteImage_DeletesFileSuccessfully() throws IOException {
        Path tempFile = Files.createTempFile("test_image", ".jpg");
        String imagePath = tempFile.toString();
        boolean result = imageService.deleteImage(imagePath);

        assertTrue(result);
        assertFalse(Files.exists(tempFile));
    }

    @Test
    void testDeleteImage_ReturnsFalseForInvalidPath() {
        boolean resultNull = imageService.deleteImage(null);
        boolean resultNonExistent = imageService.deleteImage("nonexistent_file.jpg");

        assertFalse(resultNull);
        assertFalse(resultNonExistent);
    }

    @Test
    void testSanitizeFileName_ReplacesInvalidCharacters() {
        String sanitizedFileName = imageService.sanitizeFileName("test@image#.jpg");
        assertEquals("test_image_.jpg", sanitizedFileName);
    }

    @Test
    void testSanitizeFileName_ReturnsDefaultForNull() {
        String sanitizedFileName = imageService.sanitizeFileName(null);
        assertEquals("default.png", sanitizedFileName);
    }
}
