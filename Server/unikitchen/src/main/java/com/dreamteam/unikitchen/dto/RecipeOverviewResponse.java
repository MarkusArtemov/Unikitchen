package com.dreamteam.unikitchen.dto;

import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;

public record RecipeOverviewResponse(
        Long id,
        String name,
        Double price,
        Integer duration,
        DifficultyLevel difficultyLevel,
        Category category,
        Double averageRating,
        int ratingCount,
        boolean isFavorite
) {}

