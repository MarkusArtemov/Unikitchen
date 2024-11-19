package com.dreamteam.unikitchen.dto;

import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FavoriteDTO {
    public Long recipeId;
    public String recipeName;
    public Double price;
    public Integer duration;
    public DifficultyLevel difficultyLevel;
    public Category category;
    public String recipeImagePath;
}