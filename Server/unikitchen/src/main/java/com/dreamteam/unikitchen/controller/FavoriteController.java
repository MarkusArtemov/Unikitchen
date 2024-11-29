package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
import com.dreamteam.unikitchen.facade.FavoriteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteFacade favoriteFacade;

    @Autowired
    public FavoriteController(FavoriteFacade favoriteFacade) {
        this.favoriteFacade = favoriteFacade;
    }

    @PostMapping("/current/{recipeId}")  // Adds a recipe to the user favorites
    public ResponseEntity<String> addFavorite(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }
        favoriteFacade.addFavorite(recipeId, principal.getName());  // Adds favorite for the current user
        return ResponseEntity.ok("Rezept zu Favoriten hinzugefügt");
    }

    @DeleteMapping("/current/{recipeId}")  // Removes a recipe from the user favorites
    public ResponseEntity<String> removeFavorite(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }
        favoriteFacade.removeFavorite(recipeId, principal.getName());
        return ResponseEntity.ok("Rezept aus Favoriten entfernt");
    }

    @GetMapping("/current")
    public ResponseEntity<List<FavoriteDTO>> getFavorites(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<FavoriteDTO> favorites = favoriteFacade.getFavoritesByUser(principal.getName());
        return ResponseEntity.ok(favorites);
    }
}

