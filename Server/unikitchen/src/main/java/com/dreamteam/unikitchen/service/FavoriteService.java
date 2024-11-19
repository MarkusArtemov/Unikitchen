package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
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

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public void addFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Favorite favorite = new Favorite(null, user, new Recipe(recipeId)); // Hier wird die Rezept-ID verwendet
        favoriteRepository.save(favorite);
    }
    @Transactional
    public void removeFavorite(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        favoriteRepository.deleteByUserIdAndRecipeId(user.getId(), recipeId);
    }

    public List<FavoriteDTO> getFavoritesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());

        List<FavoriteDTO> favoriteDTOs = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Recipe recipe = favorite.getRecipe();
            FavoriteDTO dto = new FavoriteDTO(
                    recipe.getId(),
                    recipe.getName(),
                    recipe.getPrice(),
                    recipe.getDuration(),
                    recipe.getDifficultyLevel(),
                    recipe.getCategory(),
                    recipe.getRecipeImagePath()
            );
            favoriteDTOs.add(dto);
        }
        return favoriteDTOs;
    }

}
