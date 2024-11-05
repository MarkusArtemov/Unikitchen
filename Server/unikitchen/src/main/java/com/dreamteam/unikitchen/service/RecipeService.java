package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    // Rezept erstellen
    public Recipe createRecipe(Recipe recipe, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        recipe.setUser(user);

        if (recipe.getIngredients() != null) {
            recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
        }

        validateRecipe(recipe);
        return recipeRepository.save(recipe);
    }

    // Rezept aktualisieren
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!existingRecipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        validateRecipe(updatedRecipe);
        updatedRecipe.setId(recipeId);
        updatedRecipe.setUser(existingRecipe.getUser());

        if (updatedRecipe.getIngredients() != null) {
            updatedRecipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(updatedRecipe));
        }

        return recipeRepository.save(updatedRecipe);
    }

    // Neue Methode für die letzten 10 Rezepte
    public List<Recipe> getLast10Recipes() {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // Rezept löschen
    public void deleteRecipe(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        recipeRepository.delete(recipe);
    }

    // Alle Rezepte des Benutzers abrufen
    public List<Recipe> getAllRecipesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return recipeRepository.findByUserId(user.getId());
    }

    // Alle Rezepte abrufen
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Rezept nach ID abrufen (ohne Benutzerüberprüfung)
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
        if (recipe.getDifficultyLevel() == null) {
            throw new IllegalArgumentException("Difficulty level is required");
        }
        if (recipe.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
    }
}
