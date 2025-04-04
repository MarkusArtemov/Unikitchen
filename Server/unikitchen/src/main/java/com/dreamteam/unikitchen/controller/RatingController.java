package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RatingInfo;
import com.dreamteam.unikitchen.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // Creates or updates a rating for a recipe
    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<RatingInfo> createOrUpdateRating(
            @PathVariable Long recipeId,
            @RequestParam("ratingValue") int ratingValue) {

        if (ratingValue < 1 || ratingValue > 5) {
            return ResponseEntity.badRequest().build();
        }

        RatingInfo ratingDTO = ratingService.createOrUpdateRating(recipeId, ratingValue);
        return ResponseEntity.status(201).body(ratingDTO);
    }

    // Gets all ratings for a given recipe
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RatingInfo>> getRatingsByRecipe(@PathVariable Long recipeId) {
        List<RatingInfo> ratings = ratingService.getRatingsByRecipe(recipeId);
        return ResponseEntity.ok(ratings);
    }

    // Gets all ratings made by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingInfo>> getRatingsByUser(@PathVariable Long userId) {
        List<RatingInfo> ratings = ratingService.getRatingsByUser(userId);
        return ResponseEntity.ok(ratings);
    }

    // Gets the current user's rating for a specific recipe
    @GetMapping("/recipe/{recipeId}/user")
    public ResponseEntity<RatingInfo> getUserRating(@PathVariable Long recipeId) {
        RatingInfo userRating = ratingService.getUserRating(recipeId);
        return ResponseEntity.ok(userRating);
    }

    // Deletes a rating by ID
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }

    // Deletes all ratings for a given recipe
    @DeleteMapping("/recipe/{recipeId}")
    public ResponseEntity<Void> deleteRatingsForRecipe(@PathVariable Long recipeId) {
        ratingService.deleteRatingsByRecipeId(recipeId);
        return ResponseEntity.noContent().build();
    }

}
