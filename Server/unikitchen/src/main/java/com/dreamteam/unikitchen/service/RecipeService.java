package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Rezept erstellen
    public Recipe createRecipe(Recipe recipe) {
        validateRecipe(recipe);
        return recipeRepository.save(recipe);
    }

    // Rezept aktualisieren
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe) {
        Optional<Recipe> existingRecipe = recipeRepository.findById(recipeId);
        if (existingRecipe.isPresent()) {
            validateRecipe(updatedRecipe);
            updatedRecipe.setId(recipeId);
            return recipeRepository.save(updatedRecipe);
        } else {
            throw new IllegalArgumentException("Recipe not found");
        }
    }

    // Rezept löschen
    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }

    // Alle Rezepte abrufen
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Rezept nach ID abrufen
    public Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }

    // Validierung der Rezepte (z.B. Pflichtfelder)
    private void validateRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            throw new IllegalArgumentException("Recipe name is required");
        }
        if (recipe.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }
        if (recipe.getDuration() == null) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (recipe.getDifficultyLevel() == null || recipe.getDifficultyLevel().isEmpty()) {
            throw new IllegalArgumentException("Difficulty level is required");
        }
    }
}
