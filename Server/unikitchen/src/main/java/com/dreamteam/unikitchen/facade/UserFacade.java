package com.dreamteam.unikitchen.facade;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.service.UserService;
import com.dreamteam.unikitchen.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserFacade {

    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public UserFacade(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    public UserInfoDTO getUserInfo(String username) {
        User user = userService.getUserEntityByUsername(username);
        return new UserInfoDTO(user.getUsername(), user.getBio(), user.getProfileImagePath());
    }

    public String uploadProfileImage(String username, byte[] imageBytes) throws IOException {
        User user = userService.getUserEntityByUsername(username);
        if (user.getProfileImagePath() != null) {
            imageService.deleteImage(user.getProfileImagePath());
        }
        String imagePath = imageService.saveImage(imageBytes);
        user.setProfileImagePath(imagePath);
        userService.updateUser(user);
        return imagePath;
    }

    public byte[] getProfileImage(String username) throws IOException {
        User user = userService.getUserEntityByUsername(username);
        return imageService.loadImage(user.getProfileImagePath());
    }

    public String updateUserProfile(String username, UserInfoDTO userInfoDTO) {
        User user = userService.getUserEntityByUsername(username);
        user.setBio(userInfoDTO.getBio());
        userService.updateUser(user);
        return "User bio updated successfully";
    }
}
