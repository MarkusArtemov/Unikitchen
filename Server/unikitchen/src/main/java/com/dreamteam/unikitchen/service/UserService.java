package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.exception.UserAlreadyExistsException;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.mapper.DTOMapper;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.UserRepository;
import com.dreamteam.unikitchen.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final DTOMapper dtoMapper;

    public UserService(UserRepository userRepository, ImageService imageService, DTOMapper dtoMapper) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.dtoMapper = dtoMapper;
    }

    // Registers a new user
    public User registerUser(String username, String password, String bio) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + username + "' already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setBio(bio);
        return userRepository.save(user);
    }

    // Authenticates a user with the given username and password
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));

        if (PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }
        throw new UserNotFoundException("Invalid username or password");
    }

    // Retrieves user information based on username
    public UserInfoDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));

        return dtoMapper.createUserInfoDTO(user);
    }

    // Uploads a profile image for the specified user
    public String uploadProfileImage(String username, MultipartFile image) throws IOException {
        User user = getUserEntityByUsername(username);

        if (user.getProfileImagePath() != null) {
            imageService.deleteImage(user.getProfileImagePath());
        }

        String imagePath = imageService.saveImage(image);
        user.setProfileImagePath(imagePath);
        updateUser(user);

        return imagePath;
    }

    // Updates the profile of the current user
    public String updateUserProfile(String username, UserInfoDTO userInfoDTO) {
        User user = getUserEntityByUsername(username);
        user.setBio(userInfoDTO.bio());
        updateUser(user);

        return "User bio updated successfully";
    }

    // Retrieves the profile image of the specified user
    public byte[] getProfileImage(String username) throws IOException {
        User user = getUserEntityByUsername(username);

        if (user.getProfileImagePath() == null) {
            throw new UserNotFoundException("No profile image found for user '" + username + "'");
        }

        return imageService.loadImage(user.getProfileImagePath());
    }

    // Retrieves user information as a DTO
    public UserInfoDTO getUserInfo(String username) {
        User user = getUserEntityByUsername(username);
        return dtoMapper.createUserInfoDTO(user);
    }

    // Updates the user entity and returns the updated information
    public UserInfoDTO updateUser(User user) {
        User updatedUser = userRepository.save(user);
        return dtoMapper.createUserInfoDTO(updatedUser);
    }

    // Retrieves a User entity by username
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
    }
}
