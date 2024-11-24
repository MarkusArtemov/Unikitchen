package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.dreamteam.unikitchen.service.ImageService;
import com.dreamteam.unikitchen.repository.RecipeRepository;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;

    @Autowired
    public RecipeController(RecipeService recipeService, RecipeRepository recipeRepository, ImageService imageService) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
        this.imageService = imageService;
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
    @GetMapping("user")
    public ResponseEntity<List<Recipe>> getAllRecipes(Principal principal) {
        List<Recipe> recipes = recipeService.getAllRecipesByUsername(principal.getName());
        return ResponseEntity.ok(recipes);
    }

    // Alle Rezepte abrufen
    @GetMapping("/allRecipes")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }


    // Neue Methode für die letzten 10 Rezepte
    @GetMapping("/lastRecipes")
    public ResponseEntity<List<Recipe>> getRecentRecipes() {
        List<Recipe> recipes = recipeService.getLast10Recipes();
        return ResponseEntity.ok(recipes);
    }

    // Rezept nach ID abrufen
    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long recipeId) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping("/{recipeId}/upload-recipe-image")
    public ResponseEntity<String> uploadRecipeImage(
            @PathVariable Long recipeId,
            @RequestParam("image") MultipartFile image,
            Principal principal) {
        try {
            // Aktuell angemeldeten Benutzer aus dem Principal holen
            String currentUsername = principal.getName();

            // Überprüfen, ob das Rezept dem aktuellen Benutzer gehört
            Optional<Recipe> recipeOptional = recipeRepository.findByRecipeIdAndUsername(recipeId, currentUsername);
            if (recipeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sie sind nicht berechtigt, dieses Rezept zu ändern");
            }

            Recipe recipe = recipeOptional.get();

            // Altes Rezeptbild löschen, falls vorhanden
            if (recipe.getRecipeImagePath() != null) {
                imageService.deleteImage(recipe.getRecipeImagePath());
            }

            // Neues Rezeptbild speichern und Pfad aktualisieren
            String imagePath = imageService.saveImage(image);
            recipe.setRecipeImagePath(imagePath);
            recipeRepository.save(recipe);

            return ResponseEntity.ok("Rezeptbild erfolgreich hochgeladen");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern des Bildes");
        }
    }



    // Rezeptbild abrufen
    @GetMapping("/{recipeId}/recipe-image")
    public ResponseEntity<byte[]> getRecipeImage(@PathVariable Long recipeId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Recipe recipe = recipeOptional.get();
        if (recipe.getRecipeImagePath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            byte[] imageBytes = imageService.loadImage(recipe.getRecipeImagePath());
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

        List<Recipe> recipes = recipeService.filterRecipes(durationCategory, difficultyLevel, category);
        return ResponseEntity.ok(recipes);
    }
}
