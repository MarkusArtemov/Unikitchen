package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
import com.dreamteam.unikitchen.factory.DTOFactory;
import com.dreamteam.unikitchen.model.Favorite;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.FavoriteRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final DTOFactory dtoFactory;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, DTOFactory dtoFactory) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.dtoFactory = dtoFactory;
    }

    public void addFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create and save a new Favorite entity
        Favorite favorite = new Favorite(null, user, new Recipe(recipeId));  // Uses recipe ID to create a new Recipe instance
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        favoriteRepository.deleteByUserIdAndRecipeId(user.getId(), recipeId);
    }

    public List<FavoriteDTO> getFavoritesByUser(String username) {
        // Fetch the user by username or throw an exception if not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Retrieve all favorites for the user and convert them to DTOs
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(dtoFactory::createFavoriteDTO)
                .toList();
    }
}
