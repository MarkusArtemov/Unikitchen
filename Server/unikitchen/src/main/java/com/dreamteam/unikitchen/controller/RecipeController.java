package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.facade.RecipeFacade;
import com.dreamteam.unikitchen.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeFacade recipeFacade;

    @Autowired
    public RecipeController(RecipeFacade recipeFacade) {
        this.recipeFacade = recipeFacade;
    }

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe, Principal principal) {
        Recipe createdRecipe = recipeFacade.createRecipe(recipe, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long recipeId, @RequestBody Recipe recipe, Principal principal) {
        Recipe updatedRecipe = recipeFacade.updateRecipe(recipeId, recipe, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(updatedRecipe);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId, Principal principal) {
        recipeFacade.deleteRecipe(recipeId, principal.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("user")
    public ResponseEntity<List<Recipe>> getAllRecipes(Principal principal) {
        List<Recipe> recipes = recipeFacade.getAllRecipesByUsername(principal.getName());
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/allRecipes")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeFacade.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/lastRecipes")
    public ResponseEntity<List<Recipe>> getRecentRecipes() {
        List<Recipe> recipes = recipeFacade.getLast10Recipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long recipeId) {
        Recipe recipe = recipeFacade.getRecipeById(recipeId);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping("/{recipeId}/upload-recipe-image")
    public ResponseEntity<String> uploadRecipeImage(
            @PathVariable Long recipeId,
            @RequestParam("image") MultipartFile image,
            Principal principal) {
        try {
            byte[] imageData = image.getBytes();
            String imagePath = recipeFacade.uploadRecipeImage(recipeId, principal.getName(), imageData);
            return ResponseEntity.ok("Rezeptbild erfolgreich hochgeladen");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern des Bildes");
        }
    }

    @GetMapping("/{recipeId}/recipe-image")
    public ResponseEntity<byte[]> getRecipeImage(@PathVariable Long recipeId) {
        try {
            byte[] imageBytes = recipeFacade.getRecipeImage(recipeId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Recipe>> getFilteredRecipes(
            @RequestParam(required = false) String durationCategory,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String category) {

        List<Recipe> recipes = recipeFacade.filterRecipes(durationCategory, difficultyLevel, category);
        return ResponseEntity.ok(recipes);
    }
}
