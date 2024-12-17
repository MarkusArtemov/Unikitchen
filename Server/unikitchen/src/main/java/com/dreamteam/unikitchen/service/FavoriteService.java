package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Favorite;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.FavoriteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final RatingService ratingService;
    private final EntityMapper entityMapper;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserService userService, RatingService ratingService, EntityMapper entityMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userService = userService;
        this.ratingService = ratingService;
        this.entityMapper = entityMapper;
    }

    @Transactional
    public boolean toggleFavorite(Long recipeId) {
        User user = userService.getUserEntity();
        Long userId = user.getId();
        Optional<Favorite> existingFavorite = Optional.ofNullable(favoriteRepository.findByUserIdAndRecipeId(userId, recipeId));
        if (existingFavorite.isPresent()) {
            favoriteRepository.deleteByUserIdAndRecipeId(user.getId(), recipeId);
        } else{
            Favorite favorite = new Favorite(null, user, new Recipe(recipeId));
            favoriteRepository.save(favorite);
        }

        return isFavorite(recipeId);

    }


    public List<RecipeOverviewResponse> getFavoritesByUser() {
        User user = userService.getUserEntity();
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(fav -> {
                    Recipe recipe = fav.getRecipe();
                    Double averageRating = ratingService.calculateAverageRating(recipe.getId());
                    int ratingCount = ratingService.getRatingCount(recipe.getId());
                    return entityMapper.toRecipeOverviewResponse(recipe, true, averageRating, ratingCount);
                })
                .toList();
    }


    public boolean isFavorite(Long recipeId) {
        User user = userService.getUserEntity();
        return favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId);
    }

    @Transactional
    public void deleteFavoritesByRecipeId(Long recipeId) {
        favoriteRepository.deleteByRecipeId(recipeId);
    }
}
