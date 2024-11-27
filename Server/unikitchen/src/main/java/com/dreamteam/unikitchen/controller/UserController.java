package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.facade.UserFacade;
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

    private final UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/current/profile-image")
    public ResponseEntity<String> uploadProfileImage(Principal principal, @RequestParam("image") MultipartFile image) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }

        try {
            String username = principal.getName();
            String imagePath = userFacade.uploadProfileImage(username, image.getBytes());
            return ResponseEntity.ok("Profilbild erfolgreich hochgeladen: " + imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern des Bildes");
        }
    }

    @GetMapping("/current/profile-image")
    public ResponseEntity<byte[]> getCurrentUserProfileImage(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            String username = principal.getName();
            byte[] imageBytes = userFacade.getProfileImage(username);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            UserInfoDTO userInfoDTO = userFacade.getUserInfo(username);
            return ResponseEntity.ok(userInfoDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
    }

    @PutMapping("/current-user")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }

        String username = principal.getName();
        String result = userFacade.updateUserProfile(username, userInfoDTO);
        return ResponseEntity.ok(result);
    }
}
