package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeCreationRequest;
import com.dreamteam.unikitchen.dto.RecipeDetailsResponse;
import com.dreamteam.unikitchen.dto.RecipeFilterRequest;
import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.dto.RecipeUpdateRequest;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Creates a new recipe
    @PostMapping
    public ResponseEntity<RecipeDetailsResponse> createRecipe(@RequestBody RecipeCreationRequest recipeCreateDTO) {
        RecipeDetailsResponse createdRecipe = recipeService.createRecipe(recipeCreateDTO);
        return ResponseEntity.status(201).body(createdRecipe);
    }

    // Retrieves the last 10 recipes
    @GetMapping("/lastRecipes")
    public ResponseEntity<List<RecipeOverviewResponse>> getRecentRecipes() {
        List<RecipeOverviewResponse> recipes = recipeService.getLast10Recipes();
        return ResponseEntity.ok(recipes);
    }

    // Updates an existing recipe
    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeDetailsResponse> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeUpdateRequest recipeUpdateDTO) {
        RecipeDetailsResponse updatedRecipe = recipeService.updateRecipe(recipeId, recipeUpdateDTO);
        return ResponseEntity.ok(updatedRecipe);
    }

    // Deletes a recipe
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

    // Retrieves all recipes from the currently logged-in user
    @GetMapping("/user")
    public ResponseEntity<List<RecipeDetailsResponse>> getAllRecipesFromUser() {
        List<RecipeDetailsResponse> recipes = recipeService.getAllRecipesByUsername();
        return ResponseEntity.ok(recipes);
    }

    // Retrieves a recipe by its ID
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDetailsResponse> getRecipeById(@PathVariable Long recipeId) {
        RecipeDetailsResponse recipeResponseDTO = recipeService.getRecipeById(recipeId);
        return ResponseEntity.ok(recipeResponseDTO);
    }

    // Uploads an image for a specific recipe
    @PostMapping("/{recipeId}/upload-recipe-image")
    public ResponseEntity<String> uploadRecipeImage(
            @PathVariable Long recipeId,
            @RequestParam("image") MultipartFile image) {
        try {
            recipeService.uploadRecipeImage(recipeId, image);
            return ResponseEntity.ok("Recipe image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error while saving the image");
        }
    }

    // Retrieves an image for a specific recipe
    @GetMapping("/{recipeId}/recipe-image")
    public ResponseEntity<byte[]> getRecipeImage(@PathVariable Long recipeId) {
        try {
            byte[] imageBytes = recipeService.getRecipeImage(recipeId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // Retrieves recipes based on filters
    @GetMapping("/filtered")
    public ResponseEntity<Page<RecipeOverviewResponse>> getFilteredRecipes(@ModelAttribute RecipeFilterRequest filterRequest) {
        Page<RecipeOverviewResponse> recipes = recipeService.getFilteredRecipes(filterRequest);
        return ResponseEntity.ok(recipes);
    }
}
