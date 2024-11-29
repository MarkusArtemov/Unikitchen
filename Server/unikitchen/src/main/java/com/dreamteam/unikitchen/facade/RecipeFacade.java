package com.dreamteam.unikitchen.facade;

import com.dreamteam.unikitchen.dto.RecipeCreateDTO;
import com.dreamteam.unikitchen.model.Ingredient;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.service.ImageService;
import com.dreamteam.unikitchen.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class RecipeFacade {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;

    @Autowired
    public RecipeFacade(RecipeService recipeService, RecipeRepository recipeRepository, ImageService imageService) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
        this.imageService = imageService;
    }

    public Recipe createRecipe(RecipeCreateDTO recipeDTO, String username) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeDTO.getName());
        recipe.setPrice(recipeDTO.getPrice());
        recipe.setDuration(recipeDTO.getDuration());
        recipe.setDifficultyLevel(recipeDTO.getDifficultyLevel());
        recipe.setCategory(recipeDTO.getCategory());
        recipe.setPreparation(recipeDTO.getPreparation());

        List<Ingredient> ingredients = recipeDTO.getIngredients().stream().map(dto -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(dto.getName());
            ingredient.setQuantity(dto.getQuantity());
            ingredient.setUnit(dto.getUnit());
            return ingredient;
        }).toList();

        recipe.setIngredients(ingredients);
        return recipeService.createRecipe(recipe, username);
    }

    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe, String username) {
        return recipeService.updateRecipe(recipeId, updatedRecipe, username);
    }

    public void deleteRecipe(Long recipeId, String username) {
        recipeService.deleteRecipe(recipeId, username);
    }

    public List<Recipe> getAllRecipesByUsername(String username) {
        return recipeService.getAllRecipesByUsername(username);
    }

    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    public Recipe getRecipeById(Long recipeId) {
        return recipeService.getRecipeById(recipeId);
    }

    public List<Recipe> getLast10Recipes() {
        return recipeService.getLast10Recipes();
    }

    public List<Recipe> filterRecipes(int durationCategory, String difficultyLevel, String category) {
        return recipeService.filterRecipes(durationCategory, difficultyLevel, category);
    }

    public String uploadRecipeImage(Long recipeId, String currentUsername, MultipartFile image) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        if (recipe.getRecipeImagePath() != null) {
            imageService.deleteImage(recipe.getRecipeImagePath());
        }

        String imagePath = imageService.saveImage(image);
        recipe.setRecipeImagePath(imagePath);
        recipeRepository.save(recipe);
        return imagePath;
    }

    public byte[] getRecipeImage(Long recipeId) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (recipe.getRecipeImagePath() == null) {
            throw new IllegalArgumentException("No image available for this recipe");
        }

        return imageService.loadImage(recipe.getRecipeImagePath());
    }
}
