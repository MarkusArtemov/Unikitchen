package com.dreamteam.unikitchen.dto;

import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;

public record FavoriteDTO(
        Long recipeId,
        String recipeName,
        Double price,
        Integer duration,
        DifficultyLevel difficultyLevel,
        Category category,
        String recipeImagePath
) {}
