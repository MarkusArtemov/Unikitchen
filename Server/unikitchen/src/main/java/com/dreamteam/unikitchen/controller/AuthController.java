package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.dto.UserRegisterDTO;
import com.dreamteam.unikitchen.dto.AuthRequest;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.service.UserService;
import com.dreamteam.unikitchen.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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
}
