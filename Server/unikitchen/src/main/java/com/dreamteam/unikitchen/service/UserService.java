package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password, String bio) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword(password));
        user.setBio(bio);
        return userRepository.save(user);
    }


    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);


        if (user != null && checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}


