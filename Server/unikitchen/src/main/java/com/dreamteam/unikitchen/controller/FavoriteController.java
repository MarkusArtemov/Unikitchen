package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // Toggles favorite status of recipe
    @PutMapping("/toggle/{recipeId}")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(@PathVariable Long recipeId) {
        boolean isFavorite = favoriteService.toggleFavorite(recipeId);
        Map<String, Boolean> response = Map.of("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    // Retrieves a list of the users favorite recipes
    @GetMapping("/current")
    public ResponseEntity<List<RecipeOverviewResponse>> getFavorites() {
        List<RecipeOverviewResponse> favorites = favoriteService.getFavoritesByUser();
        return ResponseEntity.ok(favorites);
    }
}
