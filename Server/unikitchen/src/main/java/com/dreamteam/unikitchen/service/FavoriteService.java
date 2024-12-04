package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.mapper.DTOMapper;
import com.dreamteam.unikitchen.model.Favorite;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.FavoriteRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, DTOMapper dtoMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    // Adds a recipe to the user's favorites
    public void addFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));

        // Create and save a new Favorite entity
        Favorite favorite = new Favorite(null, user, new Recipe(recipeId));
        favoriteRepository.save(favorite);
    }

    // Removes a recipe from the user's favorites
    @Transactional
    public void removeFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));

        favoriteRepository.deleteByUserIdAndRecipeId(user.getId(), recipeId);
    }

    // Retrieves all favorite recipes for the given user
    public List<FavoriteDTO> getFavoritesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));

        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(dtoMapper::createFavoriteDTO)
                .toList();
    }

    // Checks if a recipe is favorited by the user
    public boolean isFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));

        return favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId);
    }
}
