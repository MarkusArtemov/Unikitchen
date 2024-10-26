package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.IngredientCreateDTO;
import com.dreamteam.unikitchen.dto.IngredientResponseDTO;
import com.dreamteam.unikitchen.model.Ingredient;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.repository.IngredientRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    // Zutat zu einem Rezept hinzufügen
    public IngredientResponseDTO addIngredientToRecipe(Long recipeId, IngredientCreateDTO ingredientCreateDTO) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientCreateDTO.getName());
        ingredient.setQuantity(ingredientCreateDTO.getQuantity());
        ingredient.setUnit(ingredientCreateDTO.getUnit());
        ingredient.setRecipe(recipe);

        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        return new IngredientResponseDTO(
                savedIngredient.getId(),
                savedIngredient.getName(),
                savedIngredient.getQuantity(),
                savedIngredient.getUnit(),
                recipe.getId()
        );
    }

    // Zutaten eines Rezepts abrufen
    public List<IngredientResponseDTO> getIngredientsByRecipeId(Long recipeId) {
        return ingredientRepository.findByRecipeId(recipeId).stream()
                .map(ingredient -> new IngredientResponseDTO(
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getQuantity(),
                        ingredient.getUnit(),
                        ingredient.getRecipe().getId()
                ))
                .collect(Collectors.toList());
    }

    // Eine spezifische Zutat nach recipeId und ingredientId abrufen
    public IngredientResponseDTO getIngredientById(Long recipeId, Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        if (!ingredient.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("Ingredient does not belong to the specified recipe");
        }

        return new IngredientResponseDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnit(),
                ingredient.getRecipe().getId()
        );
    }

    // Zutat aktualisieren
    public IngredientResponseDTO updateIngredient(Long ingredientId, IngredientCreateDTO ingredientCreateDTO) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        ingredient.setName(ingredientCreateDTO.getName());
        ingredient.setQuantity(ingredientCreateDTO.getQuantity());
        ingredient.setUnit(ingredientCreateDTO.getUnit());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        return new IngredientResponseDTO(
                updatedIngredient.getId(),
                updatedIngredient.getName(),
                updatedIngredient.getQuantity(),
                updatedIngredient.getUnit(),
                updatedIngredient.getRecipe().getId()
        );
    }

    // Zutat löschen
    public void deleteIngredient(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        ingredientRepository.delete(ingredient);
    }
}
