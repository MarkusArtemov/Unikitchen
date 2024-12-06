package com.dreamteam.unikitchen.dto;

import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;

import java.util.List;

public record RecipeResponseDTO(
        Long id,
        String name,
        Double price,
        Integer duration,
        DifficultyLevel difficultyLevel,
        Category category,
        String preparation,
        List<IngredientCreateDTO> ingredients,
        Double averageRating,
        boolean isFavorite,
        int viewCount,
        String ownerUsername,
        int ratingCount
) {}

