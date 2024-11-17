package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.service.UserService;
import com.dreamteam.unikitchen.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public UserController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @PostMapping("/current/profile-image")
    public ResponseEntity<String> uploadProfileImage(Principal principal, @RequestParam("image") MultipartFile image) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }

        String username = principal.getName();
        try {
            // Benutzer basierend auf dem Benutzernamen abrufen
            User user = userService.getUserEntityByUsername(username);
            // Altes Profilbild löschen, falls vorhanden
            if (user.getProfileImagePath() != null) {
                imageService.deleteImage(user.getProfileImagePath());
            }
            // Neues Profilbild speichern
            String imagePath = imageService.saveImage(image);
            user.setProfileImagePath(imagePath);
            userService.updateUser(user);

            return ResponseEntity.ok("Profilbild erfolgreich hochgeladen");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern des Bildes");
        }
    }

    @GetMapping("/current/profile-image")
    public ResponseEntity<byte[]> getCurrentUserProfileImage(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = principal.getName();
        try {
            User user = userService.getUserEntityByUsername(username);
            if (user.getProfileImagePath() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = imageService.loadImage(user.getProfileImagePath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            UserInfoDTO userInfoDTO = userService.findByUsername(username);
            if (userInfoDTO != null) {
                return ResponseEntity.ok(userInfoDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
    }

    @PutMapping("/current-user")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }

        String username = principal.getName();
        try {
            User user = userService.getUserEntityByUsername(username);
            user.setBio(userInfoDTO.getBio());  // Update the bio or other fields as needed
            userService.updateUser(user);
            return ResponseEntity.ok("User bio updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}
