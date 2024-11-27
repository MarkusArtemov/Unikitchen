package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.Recipe;

import java.util.List;

public interface RecipeServiceInterface {

    Recipe createRecipe(Recipe recipe, String username);

    Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username);

    void deleteRecipe(Long recipeId, String username);

    List<Recipe> getAllRecipesByUsername(String username);

    List<Recipe> getAllRecipes();

    Recipe getRecipeById(Long recipeId);

    List<Recipe> getLast10Recipes();

    List<Recipe> filterRecipes(String durationCategory, String difficultyLevel, String category);
}
