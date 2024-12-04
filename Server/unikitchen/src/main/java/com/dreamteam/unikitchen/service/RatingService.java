package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.RatingDTO;
import com.dreamteam.unikitchen.mapper.DTOMapper;
import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RatingRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public RatingService(RatingRepository ratingRepository, RecipeRepository recipeRepository, UserRepository userRepository, DTOMapper dtoMapper) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    public RatingDTO createOrUpdateRating(Long recipeId, String username, int value) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        // Check if a rating already exists for this user and recipe
        Rating rating = ratingRepository.findByUserAndRecipe(user, recipe).orElse(null);

        if (rating == null) {
            // Create a new rating if not found
            rating = new Rating();
            rating.setUser(user);
            rating.setRecipe(recipe);
        }

        // Update the rating value
        rating.setRatingValue(value);
        Rating savedRating = ratingRepository.save(rating);

        return dtoMapper.createRatingDTO(savedRating);
    }

    public List<RatingDTO> getRatingsByRecipe(Long recipeId) {
        return ratingRepository.findByRecipeId(recipeId).stream()
                .map(dtoMapper::createRatingDTO) // Use DTOFactory
                .toList();
    }

    public List<RatingDTO> getRatingsByUser(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(dtoMapper::createRatingDTO) // Use DTOFactory
                .toList();
    }

    public void deleteRating(Long ratingId, String username) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));

        if (!rating.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this rating");
        }

        ratingRepository.delete(rating);
    }

    public Double calculateAverageRating(Long recipeId) {
        List<Rating> ratings = ratingRepository.findByRecipeId(recipeId);

        if (ratings.isEmpty()) {
            return null;
        }

        return ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }
}

