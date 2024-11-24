package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.AuthRequest;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.factory.DTOFactory;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.service.UserService;
import com.dreamteam.unikitchen.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final DTOFactory dtoFactory;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, DTOFactory dtoFactory) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.dtoFactory = dtoFactory;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        try {
            // Registriere Benutzer und erhalte das User-Objekt
            User user = userService.registerUser(
                    authRequest.getUsername(),
                    authRequest.getPassword(),
                    authRequest.getBio()
            );

            // Konvertiere User in DTO und sende es zurück
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(dtoFactory.createUserInfoDTO(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Fehler: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authentifiziere Benutzer und erhalte das User-Objekt
            User user = userService.loginUser(
                    authRequest.getUsername(),
                    authRequest.getPassword()
            );

            if (user != null) {
                // Generiere JWT-Token
                String token = jwtUtil.generateToken(user.getUsername());

                // Konvertiere User in DTO und erstelle AuthResponse
                AuthResponse authResponse = new AuthResponse(
                        token,
                        dtoFactory.createUserInfoDTO(user)
                );

                return ResponseEntity.ok(authResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Ungültiger Benutzername oder Passwort");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Fehler: " + e.getMessage());
        }
    }
}

