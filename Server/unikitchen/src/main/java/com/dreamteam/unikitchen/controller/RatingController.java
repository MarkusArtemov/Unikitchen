package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RatingDTO;
import com.dreamteam.unikitchen.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // Bewertung für ein Rezept erstellen oder aktualisieren
    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<RatingDTO> createOrUpdateRating(
            @PathVariable Long recipeId,
            @RequestParam("ratingValue") int ratingValue,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (ratingValue < 1 || ratingValue > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String username = principal.getName();
        RatingDTO ratingDTO = ratingService.createOrUpdateRating(recipeId, username, ratingValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingDTO);
    }

    // Alle Bewertungen für ein Rezept abrufen
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByRecipe(@PathVariable Long recipeId) {
        List<RatingDTO> ratings = ratingService.getRatingsByRecipe(recipeId);
        return ResponseEntity.ok(ratings);
    }

    // Bewertungen eines Benutzers abrufen
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByUser(@PathVariable Long userId) {
        List<RatingDTO> ratings = ratingService.getRatingsByUser(userId);
        return ResponseEntity.ok(ratings);
    }

    // Bewertung löschen
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        try {
            ratingService.deleteRating(ratingId, username);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}