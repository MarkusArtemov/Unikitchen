package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.Recipe;

import java.util.List;

public class RecipeValidatorDecorator implements RecipeServiceInterface {

    private final RecipeServiceInterface delegate;

    public RecipeValidatorDecorator(RecipeServiceInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    public Recipe createRecipe(Recipe recipe, String username) {
        validateRecipe(recipe);
        return delegate.createRecipe(recipe, username);
    }

    @Override
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username) {
        validateRecipe(updatedRecipe);
        return delegate.updateRecipe(recipeId, updatedRecipe, username);
    }

    @Override
    public void deleteRecipe(Long recipeId, String username) {
        delegate.deleteRecipe(recipeId, username);
    }

    @Override
    public List<Recipe> getAllRecipesByUsername(String username) {
        return delegate.getAllRecipesByUsername(username);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return delegate.getAllRecipes();
    }

    @Override
    public Recipe getRecipeById(Long recipeId) {
        return delegate.getRecipeById(recipeId);
    }

    @Override
    public List<Recipe> getLast10Recipes() {
        return delegate.getLast10Recipes();
    }

    @Override
    public List<Recipe> filterRecipes(int durationCategory, String difficultyLevel, String category) {
        return delegate.filterRecipes(durationCategory, difficultyLevel, category);
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
