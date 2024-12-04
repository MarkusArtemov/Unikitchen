package com.dreamteam.unikitchen.dto;

import java.time.LocalDateTime;

public record RatingDTO(
        Long id,
        int ratingValue,
        Long userId,
        Long recipeId,
        LocalDateTime createdAt
) {}
