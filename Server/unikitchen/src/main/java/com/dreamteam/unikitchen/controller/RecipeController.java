package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeCreateDTO;
import com.dreamteam.unikitchen.dto.RecipeResponseDTO;
import com.dreamteam.unikitchen.dto.RecipeUpdateDTO;
import com.dreamteam.unikitchen.exception.UnauthorizedAccessException;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PostMapping
    public ResponseEntity<RecipeResponseDTO> createRecipe(@RequestBody RecipeCreateDTO recipeCreateDTO, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        RecipeResponseDTO createdRecipe = recipeService.createRecipe(recipeCreateDTO, principal.getName());
        return ResponseEntity.status(201).body(createdRecipe);
    }

    @GetMapping("/allRecipes")
    public ResponseEntity<Page<RecipeResponseDTO>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        Page<RecipeResponseDTO> recipes = recipeService.getAllRecipes(principal.getName(),
                org.springframework.data.domain.PageRequest.of(
                        page, size,
                        (direction.equalsIgnoreCase("ASC") ? org.springframework.data.domain.Sort.Direction.ASC : org.springframework.data.domain.Sort.Direction.DESC),
                        (sortBy == null || sortBy.isEmpty()) ? "createdAt" : sortBy
                ));

        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/lastRecipes")
    public ResponseEntity<List<RecipeResponseDTO>> getLastRecipes(Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        List<RecipeResponseDTO> recipes = recipeService.getLast10Recipes(username);
        return ResponseEntity.ok(recipes);
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(@PathVariable Long recipeId,
                                                          @RequestBody RecipeUpdateDTO recipeUpdateDTO,
                                                          Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        RecipeResponseDTO updatedRecipe = recipeService.updateRecipe(recipeId, recipeUpdateDTO, principal.getName());
        return ResponseEntity.ok(updatedRecipe);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }
        recipeService.deleteRecipe(recipeId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<Page<RecipeResponseDTO>> getAllRecipesFromUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        Page<RecipeResponseDTO> recipes = recipeService.getAllRecipesByUsername(principal.getName(),
                org.springframework.data.domain.PageRequest.of(
                        page, size,
                        (direction.equalsIgnoreCase("ASC") ? org.springframework.data.domain.Sort.Direction.ASC : org.springframework.data.domain.Sort.Direction.DESC),
                        (sortBy == null || sortBy.isEmpty()) ? "createdAt" : sortBy
                ));

        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long recipeId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        RecipeResponseDTO recipeResponseDTO = recipeService.getRecipeById(recipeId, principal.getName());
        return ResponseEntity.ok(recipeResponseDTO);
    }

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

    @GetMapping("/{recipeId}/recipe-image")
    public ResponseEntity<byte[]> getRecipeImage(@PathVariable Long recipeId) {
        try {
            byte[] imageBytes = recipeService.getRecipeImage(recipeId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<RecipeResponseDTO>> getFilteredRecipes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean cheap,
            @RequestParam(required = false) Boolean quick,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("No user is currently logged in");
        }

        Page<RecipeResponseDTO> recipes = recipeService.getFilteredRecipes(category, cheap, quick, difficultyLevel, sortBy, direction, page, size, principal.getName());
        return ResponseEntity.ok(recipes);
    }
}
