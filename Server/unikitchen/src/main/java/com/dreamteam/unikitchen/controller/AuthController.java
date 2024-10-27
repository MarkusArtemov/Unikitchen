package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.dto.UserRegisterDTO;
import com.dreamteam.unikitchen.dto.AuthRequest;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.service.UserService;
import com.dreamteam.unikitchen.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.dreamteam.unikitchen.service.ImageService;

import java.security.Principal;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, ImageService imageService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.imageService = imageService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            UserInfoDTO userInfoDTO = userService.registerUser(
                    userRegisterDTO.getUsername(),
                    userRegisterDTO.getPassword(),
                    userRegisterDTO.getBio()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(userInfoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fehler: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        UserInfoDTO userInfoDTO = userService.loginUser(authRequest.getUsername(), authRequest.getPassword());
        if (userInfoDTO != null) {
            String token = jwtUtil.generateToken(userInfoDTO.getUsername());
            AuthResponse authResponse = new AuthResponse(token, userInfoDTO);
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiger Benutzername oder Passwort");
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

    // Endpunkt zum Hochladen eines Profilbilds
    @PostMapping("/upload-profile-image")
    public ResponseEntity<String> uploadProfileImage(Principal principal, @RequestParam("image") MultipartFile image) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }

        String username = principal.getName();
        try {
            // Versuche, den Benutzer direkt über die existierende Methode zu finden.
            UserInfoDTO userInfoDTO = userService.findByUsername(username);

            // Hole den vollständigen Benutzer, um das Bild zu speichern
            User user = userService.getUserEntityByUsername(username);
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

    @GetMapping("/current-user/profile-image")
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

}
