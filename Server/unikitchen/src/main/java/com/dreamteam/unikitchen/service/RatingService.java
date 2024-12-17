package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.context.CurrentUserContext;
import com.dreamteam.unikitchen.dto.RatingInfo;
import com.dreamteam.unikitchen.exception.RatingNotFoundException;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RatingRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Autowired
    public RatingService(RatingRepository ratingRepository, RecipeRepository recipeRepository, UserRepository userRepository, EntityMapper entityMapper) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }

    public RatingInfo createOrUpdateRating(Long recipeId, int value) {
        String username = CurrentUserContext.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Rating rating = ratingRepository.findByUserAndRecipe(user, recipe).orElse(null);

        if (rating == null) {
            rating = new Rating();
            rating.setUser(user);
            rating.setRecipe(recipe);
        }

        rating.setRatingValue(value);
        Rating savedRating = ratingRepository.save(rating);

        return entityMapper.toRatingInfo(savedRating);
    }

    public List<RatingInfo> getRatingsByRecipe(Long recipeId) {
        return ratingRepository.findByRecipeId(recipeId).stream()
                .map(entityMapper::toRatingInfo)
                .toList();
    }

    public List<RatingInfo> getRatingsByUser(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(entityMapper::toRatingInfo)
                .toList();
    }

    public void deleteRating(Long ratingId) {
        String username = CurrentUserContext.getCurrentUsername();
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));

        if (!rating.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this rating");
        }

        ratingRepository.delete(rating);
    }

    @Transactional
    public void deleteRatingsByRecipeId(Long recipeId) {
        ratingRepository.deleteByRecipeId(recipeId);
    }


    public RatingInfo getUserRating(Long recipeId) {
        String username = CurrentUserContext.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));

        Rating rating = ratingRepository.findByUserAndRecipe(user, recipe)
                .orElseThrow(() -> new RatingNotFoundException("No rating found for this user and recipe"));

        return entityMapper.toRatingInfo(rating);
    }


    public Double calculateAverageRating(Long recipeId) {
        return ratingRepository.findByRecipeId(recipeId)
                .stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }

    public int getRatingCount(Long recipeId) {
        return ratingRepository.countByRecipeId(recipeId);
    }
}
