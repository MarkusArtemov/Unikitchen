package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeCreateDTO;
import com.dreamteam.unikitchen.dto.RecipeResponseDTO;
import com.dreamteam.unikitchen.dto.RecipeUpdateDTO;
import com.dreamteam.unikitchen.exception.UnauthorizedAccessException;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Creates a new recipe
    @PostMapping
    public ResponseEntity<RecipeResponseDTO> createRecipe(@RequestBody RecipeCreateDTO recipeCreateDTO, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        RecipeResponseDTO createdRecipe = recipeService.createRecipe(recipeCreateDTO, principal.getName());
        return ResponseEntity.status(201).body(createdRecipe);
    }

    // Retrieves all recipes
    @GetMapping("/allRecipes")
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipes(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        List<RecipeResponseDTO> recipes = recipeService.getAllRecipes(principal.getName());
        return ResponseEntity.ok(recipes);
    }

    // Retrieves the last 10 recipes
    @GetMapping("/lastRecipes")
    public ResponseEntity<List<RecipeResponseDTO>> getRecentRecipes(Principal principal) {
        // If the principal is null, set username to null (unauthenticated user)
        String username = (principal != null) ? principal.getName() : null;

        // Call the service method with the username (null for unauthenticated)
        List<RecipeResponseDTO> recipes = recipeService.getLast10Recipes(username);

        return ResponseEntity.ok(recipes);
    }


    // Updates an existing recipe
    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeUpdateDTO recipeUpdateDTO, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        RecipeResponseDTO updatedRecipe = recipeService.updateRecipe(recipeId, recipeUpdateDTO, principal.getName());
        return ResponseEntity.ok(updatedRecipe);
    }

    // Deletes a recipe
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        recipeService.deleteRecipe(recipeId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // Retrieves all recipes from the currently logged-in user
    @GetMapping("user")
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipesFromUser(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        List<RecipeResponseDTO> recipes = recipeService.getAllRecipesByUsername(principal.getName());
        return ResponseEntity.ok(recipes);
    }

    // Retrieves a recipe by its ID
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        RecipeResponseDTO recipeResponseDTO = recipeService.getRecipeById(recipeId, principal.getName());
        return ResponseEntity.ok(recipeResponseDTO);
    }

    // Uploads an image for a specific recipe
    @PostMapping("/{recipeId}/upload-recipe-image")
    public ResponseEntity<String> uploadRecipeImage(
            @PathVariable Long recipeId,
            @RequestParam("image") MultipartFile image,
            Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        try {
            recipeService.uploadRecipeImage(recipeId, principal.getName(), image);
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
    public ResponseEntity<List<RecipeResponseDTO>> getFilteredRecipes(
            @RequestParam(required = false) int durationCategory,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String category,
            Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        List<RecipeResponseDTO> recipes = recipeService.filterRecipes(durationCategory, difficultyLevel, category, principal.getName());
        return ResponseEntity.ok(recipes);
    }
}
