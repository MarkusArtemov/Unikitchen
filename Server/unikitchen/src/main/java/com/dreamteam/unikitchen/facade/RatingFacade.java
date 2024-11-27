package com.dreamteam.unikitchen.facade;

import com.dreamteam.unikitchen.dto.RatingDTO;
import com.dreamteam.unikitchen.service.RatingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RatingFacade {

    private final RatingService ratingService;

    public RatingFacade(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public RatingDTO createOrUpdateRating(Long recipeId, String username, int ratingValue) {
        return ratingService.createOrUpdateRating(recipeId, username, ratingValue);
    }

    public List<RatingDTO> getRatingsByRecipe(Long recipeId) {
        return ratingService.getRatingsByRecipe(recipeId);
    }

    public List<RatingDTO> getRatingsByUser(Long userId) {
        return ratingService.getRatingsByUser(userId);
    }

    public void deleteRating(Long ratingId, String username) {
        ratingService.deleteRating(ratingId, username);
    }
}
