package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.dto.UserResponseDTO;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dreamteam.unikitchen.dto.UserRegisterDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            User user = userService.registerUser(userRegisterDTO.getUsername(), userRegisterDTO.getPassword(), userRegisterDTO.getBio());

            UserResponseDTO responseDTO = new UserResponseDTO(user.getId(), user.getUsername(), user.getBio());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username,
                                        @RequestParam String password,
                                        HttpSession session) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            UserInfoDTO responseDTO = new UserInfoDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getBio(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently logged in");
        }
    }

}
