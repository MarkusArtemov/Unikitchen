package com.dreamteam.unikitchen.dto;


public record IngredientResponseDTO(
        Long id,
        String name,
        Double quantity,
        String unit,
        Long recipeId
) {}

