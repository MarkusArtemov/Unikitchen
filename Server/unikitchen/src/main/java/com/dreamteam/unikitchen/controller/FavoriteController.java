package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
import com.dreamteam.unikitchen.exception.UnauthorizedAccessException;
import com.dreamteam.unikitchen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // Adds a recipe to the users favorites
    @PostMapping("/current/{recipeId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        favoriteService.addFavorite(recipeId, principal.getName());
        return ResponseEntity.ok("Recipe added to favorites");
    }

    // Removes a recipe from the users favorites
    @DeleteMapping("/current/{recipeId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        favoriteService.removeFavorite(recipeId, principal.getName());
        return ResponseEntity.ok("Recipe removed from favorites");
    }

    @DeleteMapping("/recipe/{recipeId}")
    public ResponseEntity<Void> deleteFavoritesForRecipe(@PathVariable Long recipeId) {
        favoriteService.deleteFavoritesByRecipeId(recipeId);
        return ResponseEntity.noContent().build();
    }


    // Retrieves a list of the users favorite recipes
    @GetMapping("/current")
    public ResponseEntity<List<FavoriteDTO>> getFavorites(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUser(principal.getName());
        return ResponseEntity.ok(favorites);
    }
}
