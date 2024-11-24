package com.dreamteam.unikitchen.factory;

import com.dreamteam.unikitchen.dto.*;
import com.dreamteam.unikitchen.model.*;

import org.springframework.stereotype.Component;

@Component
public class DTOFactory {

    public FavoriteDTO createFavoriteDTO(Favorite favorite) {
        Recipe recipe = favorite.getRecipe();
        return new FavoriteDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getPrice(),
                recipe.getDuration(),
                recipe.getDifficultyLevel(),
                recipe.getCategory(),
                recipe.getRecipeImagePath()
        );
    }

    public IngredientResponseDTO createIngredientResponseDTO(Ingredient ingredient) {
        return new IngredientResponseDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnit(),
                ingredient.getRecipe().getId()
        );
    }

    public RatingDTO createRatingDTO(Rating rating) {
        return new RatingDTO(
                rating.getId(),
                rating.getRatingValue(),
                rating.getUser().getId(),
                rating.getRecipe().getId(),
                rating.getCreatedAt()
        );
    }

    public UserInfoDTO createUserInfoDTO(User user) {
        return new UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
