package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.AuthRequest;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.mapper.DTOMapper;
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
    private final DTOMapper dtoMapper;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, DTOMapper dtoMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserInfoDTO> register(@RequestBody AuthRequest authRequest) {
        // Register the user and retrieve the User object
        User user = userService.registerUser(
                authRequest.username(),
                authRequest.password(),
                authRequest.bio()
        );

        // Convert the User object to a DTO and return it
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoMapper.createUserInfoDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // Authenticate the user and retrieve the User object
        User user = userService.loginUser(
                authRequest.username(),
                authRequest.password()
        );

        // Generate a JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Convert the User object to a DTO and create an AuthResponse
        AuthResponse authResponse = new AuthResponse(
                token,
                dtoMapper.createUserInfoDTO(user)
        );

        return ResponseEntity.ok(authResponse);
    }
}
