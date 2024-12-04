package com.dreamteam.unikitchen.dto;

public record IngredientCreateDTO(
        String name,
        Double quantity,
        String unit
) {}

