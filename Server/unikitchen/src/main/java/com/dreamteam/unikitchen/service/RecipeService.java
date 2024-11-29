package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.Ingredient;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }


    public Recipe createRecipe(Recipe recipe, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        recipe.setUser(user);  // Associate recipe with user

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.setRecipe(recipe);
        }

        // Save and return the recipe
        return recipeRepository.save(recipe);
    }


    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!existingRecipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        existingRecipe.setName(updatedRecipe.getName());
        existingRecipe.setPreparation(updatedRecipe.getPreparation());
        existingRecipe.setCategory(updatedRecipe.getCategory());
        existingRecipe.setDuration(updatedRecipe.getDuration());
        existingRecipe.setDifficultyLevel(updatedRecipe.getDifficultyLevel());

        return recipeRepository.save(existingRecipe);
    }

    public void deleteRecipe(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        recipeRepository.delete(recipe);
    }


    public List<Recipe> getAllRecipesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Return all recipes for the user
        return recipeRepository.findByUserId(user.getId());
    }

    public List<Recipe> getAllRecipes() {
        // Return all recipes in the database
        return recipeRepository.findAll();
    }


    public Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }


    public List<Recipe> getLast10Recipes() {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Recipe> filterRecipes(int durationCategory, String difficultyLevel, String category) {
        // Return recipes matching the provided filters
        return recipeRepository.findByFilters(durationCategory, difficultyLevel, category);
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            throw new IllegalArgumentException("Recipe name is required");
        }
        if (recipe.getPrice() == null || recipe.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (recipe.getDuration() == null) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (recipe.getDifficultyLevel() == null) {
            throw new IllegalArgumentException("Difficulty level is required");
        }
        if (recipe.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
    }
}

