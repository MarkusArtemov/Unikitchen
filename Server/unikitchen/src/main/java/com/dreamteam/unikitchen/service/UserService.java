package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.UserInfoDTO;
import com.dreamteam.unikitchen.factory.DTOFactory;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.UserRepository;
import com.dreamteam.unikitchen.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DTOFactory dtoFactory;

    public UserService(UserRepository userRepository, DTOFactory dtoFactory) {
        this.userRepository = userRepository;
        this.dtoFactory = dtoFactory;
    }

    // Registriere einen neuen Benutzer
    public User registerUser(String username, String password, String bio) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Benutzername existiert bereits");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setBio(bio);
        return userRepository.save(user); // Speichere und gib das User-Objekt zurück
    }

    // Anmelden eines Benutzers
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null; // Gib null zurück, wenn die Authentifizierung fehlschlägt
    }

    // Benutzerinformationen basierend auf dem Benutzernamen abrufen
    public UserInfoDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return dtoFactory.createUserInfoDTO(user);
    }

    // Benutzerinformationen aktualisieren
    public UserInfoDTO updateUser(User user) {
        User updatedUser = userRepository.save(user);
        return dtoFactory.createUserInfoDTO(updatedUser);
    }

    // Im UserService
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

}
