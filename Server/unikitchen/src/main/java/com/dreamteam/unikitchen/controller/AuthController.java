package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.AuthRequest;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.dto.UserInfoResponse;
import com.dreamteam.unikitchen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Registers a new user
    @PostMapping("/register")
    public ResponseEntity<UserInfoResponse> register(@RequestBody AuthRequest authRequest) {
        UserInfoResponse user = userService.registerUser(
                authRequest.username(),
                authRequest.password(),
                authRequest.bio()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    // Logs in an existing user
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = userService.loginUser(
                authRequest.username(),
                authRequest.password()
        );
        return ResponseEntity.ok(authResponse);
    }
}
