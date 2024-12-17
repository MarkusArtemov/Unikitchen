package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoResponse;
import com.dreamteam.unikitchen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<String> uploadProfileImage( @RequestParam("image") MultipartFile image) {
        try {
            String imagePath = userService.uploadProfileImage(image);
            return ResponseEntity.ok("Profile image uploaded successfully: " + imagePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error while saving the image");
        }
    }

    // Retrieves the profile image of the currently logged-in user
    @GetMapping("/current/profile-image")
    public ResponseEntity<byte[]> getCurrentUserProfileImage() {
        try {
            byte[] imageBytes = userService.getProfileImage();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // Retrieves information about the currently logged-in user
    @GetMapping("/current-user")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        UserInfoResponse userInfoDTO = userService.getUserInfo();
        return ResponseEntity.ok(userInfoDTO);
    }

    // Updates information for the currently logged-in user
    @PutMapping("/current-user")
    public ResponseEntity<String> updateCurrentUser(@RequestBody UserInfoResponse userInfoDTO) {
        String result = userService.updateUserProfile(userInfoDTO);
        return ResponseEntity.ok(result);
    }
}
