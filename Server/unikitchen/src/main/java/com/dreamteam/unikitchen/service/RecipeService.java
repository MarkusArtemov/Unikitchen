package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

public class RecipeService implements RecipeServiceInterface {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Recipe createRecipe(Recipe recipe, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        recipe.setUser(user);
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (!existingRecipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }
        updatedRecipe.setId(recipeId);
        return recipeRepository.save(updatedRecipe);
    }

    @Override
    public void deleteRecipe(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }
        recipeRepository.delete(recipe);
    }

    @Override
    public List<Recipe> getAllRecipesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return recipeRepository.findByUserId(user.getId());
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }

    @Override
    public List<Recipe> getLast10Recipes() {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Recipe> filterRecipes(String durationCategory, String difficultyLevel, String category) {
        Integer duration = convertDurationCategoryToMinutes(durationCategory);
        return recipeRepository.findByFilters(duration, difficultyLevel, category);
    }

    private Integer convertDurationCategoryToMinutes(String durationCategory) {
        if (durationCategory == null) return null;
        switch (durationCategory) {
            case "short":
                return 15;
            case "medium":
                return 30;
            case "long":
                return 60;
            default:
                return null;
        }
    }
}
