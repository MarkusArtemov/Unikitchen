package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.context.CurrentUserContext;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.dto.UserInfoResponse;
import com.dreamteam.unikitchen.exception.UserAlreadyExistsException;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.jwt.JwtUtil;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final EntityMapper entityMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, ImageService imageService, EntityMapper entityMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.entityMapper = entityMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserInfoResponse registerUser(String username, String password, String bio) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + username + "' already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setBio(bio);
        userRepository.save(user);

        return entityMapper.toUserInfoResponse(user);
    }

    public AuthResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));

        if (passwordEncoder.matches(password, user.getPassword())) {

            // Generate a JWT token
            String token = jwtUtil.generateToken(user.getUsername());


            // Convert the User object to a DTO and return AuthResponse
            return new AuthResponse(
                    token,
                    entityMapper.toUserInfoResponse(user)
            );

        }
        throw new UserNotFoundException("Invalid username or password");
    }

    public String uploadProfileImage(MultipartFile image) throws IOException {
        User user = getUserEntity();

        if (user.getProfileImagePath() != null) {
            imageService.deleteImage(user.getProfileImagePath());
        }

        String imagePath = imageService.saveImage(image);
        user.setProfileImagePath(imagePath);
        updateUser(user);

        return imagePath;
    }

    public String updateUserProfile(UserInfoResponse userInfoDTO) {
        User user = getUserEntity();
        user.setBio(userInfoDTO.bio());
        updateUser(user);
        return "User bio updated successfully";
    }

    public byte[] getProfileImage() throws IOException {
        User user = getUserEntity();

        if (user.getProfileImagePath() == null) {
            throw new UserNotFoundException("No profile image found for user '" + user.getUsername() + "'");
        }

        return imageService.loadImage(user.getProfileImagePath());
    }

    public UserInfoResponse getUserInfo() {
        User user = getUserEntity();
        return entityMapper.toUserInfoResponse(user);
    }

    public void updateUser(User user) {
        User updatedUser = userRepository.save(user);
        entityMapper.toUserInfoResponse(updatedUser);
    }

    public User getUserEntity() {
        String username = CurrentUserContext.getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
    }
}
