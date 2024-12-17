package com.dreamteam.unikitchen.dto;

public record RecipeFilterRequest(
        String category,
        Boolean cheap,
        Boolean quick,
        String difficultyLevel,
        int page,
        int size,
        String sortBy,
        String direction
) {
    public RecipeFilterRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (direction == null || direction.isBlank()) direction = "DESC";
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
    }
}
