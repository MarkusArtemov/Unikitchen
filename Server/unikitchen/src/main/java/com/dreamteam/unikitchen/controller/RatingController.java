package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RatingDTO;
import com.dreamteam.unikitchen.exception.UnauthorizedAccessException;
import com.dreamteam.unikitchen.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Creates or updates a rating for a specific recipe
    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<RatingDTO> createOrUpdateRating(
            @PathVariable Long recipeId,
            @RequestParam("ratingValue") int ratingValue,
            Principal principal) {

        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        if (ratingValue < 1 || ratingValue > 5) {
            return ResponseEntity.badRequest().build();
        }

        String username = principal.getName();
        RatingDTO ratingDTO = ratingService.createOrUpdateRating(recipeId, username, ratingValue);
        return ResponseEntity.status(201).body(ratingDTO);
    }

    // Retrieves all ratings for a specific recipe
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByRecipe(@PathVariable Long recipeId) {
        List<RatingDTO> ratings = ratingService.getRatingsByRecipe(recipeId);
        return ResponseEntity.ok(ratings);
    }

    // Retrieves all ratings submitted by a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByUser(@PathVariable Long userId) {
        List<RatingDTO> ratings = ratingService.getRatingsByUser(userId);
        return ResponseEntity.ok(ratings);
    }

    // Deletes a rating
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        String username = principal.getName();
        ratingService.deleteRating(ratingId, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/recipe/{recipeId}")
    public ResponseEntity<Void> deleteRatingsForRecipe(@PathVariable Long recipeId) {
        ratingService.deleteRatingsByRecipeId(recipeId);
        return ResponseEntity.noContent().build();
    }

}
