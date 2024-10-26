package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.UserRepository;
import com.dreamteam.unikitchen.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Registriere einen neuen Benutzer
    public UserInfoDTO registerUser(String username, String password, String bio) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Benutzername existiert bereits");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setBio(bio);
        user = userRepository.save(user);

        return new UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Anmelden eines Benutzers
    public UserInfoDTO loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (PasswordUtil.checkPassword(password, user.getPassword())) {
            return new UserInfoDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getBio(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
        return null;
    }

    // Benutzerinformationen basierend auf dem Benutzernamen abrufen
    public UserInfoDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
