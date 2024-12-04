package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.IngredientCreateDTO;
import com.dreamteam.unikitchen.dto.IngredientResponseDTO;
import com.dreamteam.unikitchen.exception.IngredientNotFoundException;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.mapper.DTOMapper;
import com.dreamteam.unikitchen.model.Ingredient;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.repository.IngredientRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, RecipeRepository recipeRepository, DTOMapper dtoMapper) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.dtoMapper = dtoMapper;
    }

    // Adds an ingredient to a recipe
    public IngredientResponseDTO addIngredientToRecipe(Long recipeId, IngredientCreateDTO ingredientCreateDTO) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe with ID " + recipeId + " not found"));

        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientCreateDTO.name());
        ingredient.setQuantity(ingredientCreateDTO.quantity());
        ingredient.setUnit(ingredientCreateDTO.unit());
        ingredient.setRecipe(recipe);

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return dtoMapper.createIngredientResponseDTO(savedIngredient);
    }

    // Retrieves all ingredients for a given recipe
    public List<IngredientResponseDTO> getIngredientsByRecipeId(Long recipeId) {
        return ingredientRepository.findByRecipeId(recipeId).stream()
                .map(dtoMapper::createIngredientResponseDTO)
                .toList();
    }

    // Retrieves a specific ingredient by recipeId and ingredientId
    public IngredientResponseDTO getIngredientById(Long recipeId, Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IngredientNotFoundException("Ingredient with ID " + ingredientId + " not found"));

        if (!ingredient.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("Ingredient does not belong to the specified recipe");
        }

        return dtoMapper.createIngredientResponseDTO(ingredient);
    }

    // Updates an existing ingredient
    public IngredientResponseDTO updateIngredient(Long ingredientId, IngredientCreateDTO ingredientCreateDTO) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IngredientNotFoundException("Ingredient with ID " + ingredientId + " not found"));

        ingredient.setName(ingredientCreateDTO.name());
        ingredient.setQuantity(ingredientCreateDTO.quantity());
        ingredient.setUnit(ingredientCreateDTO.unit());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        return dtoMapper.createIngredientResponseDTO(updatedIngredient);
    }

    // Deletes an ingredient
    public void deleteIngredient(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IngredientNotFoundException("Ingredient with ID " + ingredientId + " not found"));

        ingredientRepository.delete(ingredient);
    }
}
