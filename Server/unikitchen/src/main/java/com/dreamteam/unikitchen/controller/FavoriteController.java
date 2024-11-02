package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
import com.dreamteam.unikitchen.model.Favorite;
import com.dreamteam.unikitchen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/current/{recipeId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }
        favoriteService.addFavorite(recipeId, principal.getName());
        return ResponseEntity.ok("Rezept zu Favoriten hinzugefügt");
    }

    @DeleteMapping("/current/{recipeId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Benutzer ist aktuell angemeldet");
        }
        favoriteService.removeFavorite(recipeId, principal.getName());
        return ResponseEntity.ok("Rezept aus Favoriten entfernt");
    }

    @GetMapping("/current")
    public ResponseEntity<List<FavoriteDTO>> getFavorites(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUser(principal.getName());
        return ResponseEntity.ok(favorites);
    }
}
