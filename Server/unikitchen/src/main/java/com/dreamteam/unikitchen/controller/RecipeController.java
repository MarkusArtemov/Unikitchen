package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Rezept erstellen
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe, Principal principal) {
        Recipe createdRecipe = recipeService.createRecipe(recipe, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    // Rezept aktualisieren
    @PutMapping("/{recipeId}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long recipeId, @RequestBody Recipe recipe, Principal principal) {
        Recipe updatedRecipe = recipeService.updateRecipe(recipeId, recipe, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(updatedRecipe);
    }

    // Rezept löschen
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId, Principal principal) {
        recipeService.deleteRecipe(recipeId, principal.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Alle Rezepte des angemeldeten Benutzers abrufen
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(Principal principal) {
        List<Recipe> recipes = recipeService.getAllRecipesByUsername(principal.getName());
        return ResponseEntity.ok(recipes);
    }

    // Rezept nach ID abrufen und Überprüfung des Besitzers
    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long recipeId, Principal principal) {
        Recipe recipe = recipeService.getRecipeByIdAndUsername(recipeId, principal.getName());
        return ResponseEntity.ok(recipe);
    }
}
