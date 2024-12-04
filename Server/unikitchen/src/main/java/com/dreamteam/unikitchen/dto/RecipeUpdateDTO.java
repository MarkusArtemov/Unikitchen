package com.dreamteam.unikitchen.dto;

import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;

import java.util.List;

public record RecipeUpdateDTO(
        Long id,
        String name,
        Double price,
        Integer duration,
        DifficultyLevel difficultyLevel,
        Category category,
        String preparation,
        List<IngredientCreateDTO> ingredients
) {}

