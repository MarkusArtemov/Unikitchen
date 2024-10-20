package com.dreamteam.unikitchen.controller;
import com.dreamteam.unikitchen.dto.IngredientCreateDTO;
import com.dreamteam.unikitchen.dto.IngredientResponseDTO;
import com.dreamteam.unikitchen.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    public ResponseEntity<IngredientResponseDTO> addIngredient(@PathVariable Long recipeId, @RequestBody IngredientCreateDTO ingredientCreateDTO) {
        IngredientResponseDTO responseDTO = ingredientService.addIngredientToRecipe(recipeId, ingredientCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(@PathVariable Long ingredientId, @RequestBody IngredientCreateDTO ingredientCreateDTO) {
        IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(ingredientId, ingredientCreateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedIngredient);
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponseDTO>> getIngredientsByRecipe(@PathVariable Long recipeId) {
        List<IngredientResponseDTO> ingredients = ingredientService.getIngredientsByRecipeId(recipeId);
        return ResponseEntity.status(HttpStatus.OK).body(ingredients);
    }

    @GetMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponseDTO> getIngredientById(@PathVariable Long recipeId, @PathVariable Long ingredientId) {
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(recipeId, ingredientId);
        return ResponseEntity.status(HttpStatus.OK).body(ingredient);
    }

    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long ingredientId) {
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}