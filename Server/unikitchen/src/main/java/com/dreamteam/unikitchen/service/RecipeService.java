package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.IngredientCreateDTO;
import com.dreamteam.unikitchen.dto.RecipeCreateDTO;
import com.dreamteam.unikitchen.dto.RecipeResponseDTO;
import com.dreamteam.unikitchen.dto.RecipeUpdateDTO;
import com.dreamteam.unikitchen.mapper.DTOMapper;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final DTOMapper dtoMapper;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, DTOMapper dtoMapper,
                         RatingService ratingService, FavoriteService favoriteService,
                         UserService userService, ImageService imageService) {
        this.recipeRepository = recipeRepository;
        this.dtoMapper = dtoMapper;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.imageService = imageService;
    }

    public RecipeResponseDTO createRecipe(RecipeCreateDTO recipeCreateDTO, String username) {
        User user = userService.getUserEntityByUsername(username);

        Recipe recipe = dtoMapper.mapToRecipeEntity(recipeCreateDTO, user);

        validateRecipe(recipe);

        recipeRepository.save(recipe);

        return buildRecipeResponseDTO(recipe, username);
    }

    public RecipeResponseDTO updateRecipe(Long recipeId, RecipeUpdateDTO updatedRecipe, String username) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!existingRecipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        var updatedIngredients = dtoMapper.mapToIngredientList(updatedRecipe.ingredients(), existingRecipe);

        existingRecipe.setName(updatedRecipe.name());
        existingRecipe.setPreparation(updatedRecipe.preparation());
        existingRecipe.setCategory(updatedRecipe.category());
        existingRecipe.setDuration(updatedRecipe.duration());
        existingRecipe.setDifficultyLevel(updatedRecipe.difficultyLevel());
        existingRecipe.setIngredients(updatedIngredients);

        validateRecipe(existingRecipe);

        recipeRepository.save(existingRecipe);

        return buildRecipeResponseDTO(existingRecipe, username);
    }

    public void deleteRecipe(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        recipeRepository.delete(recipe);
    }

    public List<RecipeResponseDTO> getAllRecipesByUsername(String username) {
        User user = userService.getUserEntityByUsername(username);

        return recipeRepository.findByUserId(user.getId()).stream()
                .map(recipe -> buildRecipeResponseDTO(recipe, username))
                .toList();
    }

    public List<RecipeResponseDTO> getAllRecipes(String username) {
        return recipeRepository.findAll().stream()
                .map(recipe -> buildRecipeResponseDTO(recipe, username))
                .toList();
    }

    public RecipeResponseDTO getRecipeById(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(username)) {
            recipe.setViewCount(recipe.getViewCount() + 1);
            recipeRepository.save(recipe);
        }

        return buildRecipeResponseDTO(recipe, username);
    }


    public List<RecipeResponseDTO> getLast10Recipes(String username) {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(recipe -> {
                    boolean isFavorite = username != null && favoriteService.isFavorite(recipe.getId(), username);
                    Double averageRating = ratingService.calculateAverageRating(recipe.getId());
                    int ratingCount = ratingService.getRatingCount(recipe.getId());
                    return dtoMapper.mapToRecipeResponseDTO(recipe, isFavorite, averageRating, username, ratingCount);
                })
                .toList();
    }


    public List<RecipeResponseDTO> filterRecipes(int durationCategory, String difficultyLevel, String category, String username) {
        return recipeRepository.findByFilters(durationCategory, difficultyLevel, category).stream()
                .map(recipe -> buildRecipeResponseDTO(recipe, username))
                .toList();
    }

    public void uploadRecipeImage(Long recipeId, String currentUsername, MultipartFile image) throws IOException {
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
    }

    public byte[] getRecipeImage(Long recipeId) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (recipe.getRecipeImagePath() == null) {
            throw new IllegalArgumentException("No image available for this recipe");
        }

        return imageService.loadImage(recipe.getRecipeImagePath());
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            throw new IllegalArgumentException("Recipe name is required");
        }
        if (recipe.getPrice() == null || recipe.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (recipe.getDuration() == null) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (recipe.getDifficultyLevel() == null) {
            throw new IllegalArgumentException("Difficulty level is required");
        }
        if (recipe.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
    }

    private RecipeResponseDTO buildRecipeResponseDTO(Recipe recipe, String username) {
        boolean isFavorite = favoriteService.isFavorite(recipe.getId(), username);
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        String ownerUsername = recipe.getUser().getUsername();
        int ratingCount = ratingService.getRatingCount(recipe.getId());
        return dtoMapper.mapToRecipeResponseDTO(recipe, isFavorite, averageRating, ownerUsername, ratingCount);
    }
}



