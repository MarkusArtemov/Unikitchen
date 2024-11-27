package com.dreamteam.unikitchen.facade;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.service.ImageService;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RecipeFacade {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;

    @Autowired
    public RecipeFacade(RecipeService recipeService, RecipeRepository recipeRepository, ImageService imageService) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
        this.imageService = imageService;
    }

    public Recipe createRecipe(Recipe recipe, String username) {
        return recipeService.createRecipe(recipe, username);
    }

    // Rezept aktualisieren
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username) {
        return recipeService.updateRecipe(recipeId, updatedRecipe, username);
    }

    // Rezept löschen
    public void deleteRecipe(Long recipeId, String username) {
        recipeService.deleteRecipe(recipeId, username);
    }

    // Alle Rezepte des Benutzers abrufen
    public List<Recipe> getAllRecipesByUsername(String username) {
        return recipeService.getAllRecipesByUsername(username);
    }

    // Alle Rezepte abrufen
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // Rezept nach ID abrufen
    public Recipe getRecipeById(Long recipeId) {
        return recipeService.getRecipeById(recipeId);
    }

    // Letzte 10 Rezepte abrufen
    public List<Recipe> getLast10Recipes() {
        return recipeService.getLast10Recipes();
    }

    // Filter Rezepte
    public List<Recipe> filterRecipes(String durationCategory, String difficultyLevel, String category) {
        return recipeService.filterRecipes(durationCategory, difficultyLevel, category);
    }

    // Rezeptbild hochladen
    public String uploadRecipeImage(Long recipeId, String currentUsername, byte[] imageData) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        // Altes Rezeptbild löschen
        if (recipe.getRecipeImagePath() != null) {
            imageService.deleteImage(recipe.getRecipeImagePath());
        }

        // Neues Rezeptbild speichern und Pfad aktualisieren
        String imagePath = imageService.saveImage(imageData);
        recipe.setRecipeImagePath(imagePath);
        recipeRepository.save(recipe);
        return imagePath;
    }

    // Rezeptbild abrufen
    public byte[] getRecipeImage(Long recipeId) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (recipe.getRecipeImagePath() == null) {
            throw new IllegalArgumentException("No image available for this recipe");
        }

        return imageService.loadImage(recipe.getRecipeImagePath());
    }
}
