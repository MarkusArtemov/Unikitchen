package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.exception.UnauthorizedAccessException;
import com.dreamteam.unikitchen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Uploads the profile image for the currently logged-in user
    @PostMapping("/current/profile-image")
    public ResponseEntity<String> uploadProfileImage(Principal principal, @RequestParam("image") MultipartFile image) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        try {
            String username = principal.getName();
            String imagePath = userService.uploadProfileImage(username, image);
            return ResponseEntity.ok("Profile image uploaded successfully: " + imagePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error while saving the image");
        }
    }

    // Retrieves the profile image of the currently logged-in user
    @GetMapping("/current/profile-image")
    public ResponseEntity<byte[]> getCurrentUserProfileImage(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        try {
            String username = principal.getName();
            byte[] imageBytes = userService.getProfileImage(username);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // Retrieves information about the currently logged-in user
    @GetMapping("/current-user")
    public ResponseEntity<UserInfoDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        String username = principal.getName();
        UserInfoDTO userInfoDTO = userService.getUserInfo(username);
        return ResponseEntity.ok(userInfoDTO);
    }

    // Updates information for the currently logged-in user
    @PutMapping("/current-user")
    public ResponseEntity<String> updateCurrentUser(@RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        String username = principal.getName();
        String result = userService.updateUserProfile(username, userInfoDTO);
        return ResponseEntity.ok(result);
    }
}
