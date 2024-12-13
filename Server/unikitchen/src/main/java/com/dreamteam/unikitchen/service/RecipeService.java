package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.IngredientDTO;
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
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
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

        existingRecipe.setName(updatedRecipe.name());
        existingRecipe.setPreparation(updatedRecipe.preparation());
        existingRecipe.setCategory(updatedRecipe.category());
        existingRecipe.setDuration(updatedRecipe.duration());
        existingRecipe.setDifficultyLevel(updatedRecipe.difficultyLevel());
        existingRecipe.setIngredients(updatedRecipe.ingredients());

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

        imageService.deleteImage(recipe.getRecipeImagePath());
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

    public List<RecipeResponseDTO> getFilteredRecipes(String category, Boolean cheap, Boolean quick, String sortBy, String username) {
        List<Recipe> allRecipes = recipeRepository.findAll();

        if (category != null && !category.isEmpty()) {
            allRecipes = allRecipes.stream()
                    .filter(r -> r.getCategory().name().equalsIgnoreCase(category))
                    .toList();
        }

        Integer maxPrice = (Boolean.TRUE.equals(cheap)) ? 10 : null;
        Integer maxDuration = (Boolean.TRUE.equals(quick)) ? 15 : null;

        List<Recipe> filtered = allRecipes.stream()
                .filter(r -> maxPrice == null || r.getPrice() <= maxPrice)
                .filter(r -> maxDuration == null || r.getDuration() <= maxDuration)
                .toList();

        if ("popular".equalsIgnoreCase(sortBy)) {
            double maxViewCount = filtered.stream().mapToDouble(Recipe::getViewCount).max().orElse(1);
            double maxRatingCount = filtered.stream().mapToDouble(r -> ratingService.getRatingCount(r.getId())).max().orElse(1);
            long now = System.currentTimeMillis();
            filtered = filtered.stream()
                    .sorted((r1, r2) -> {
                        double score1 = calculatePopularity(r1, username, maxViewCount, maxRatingCount, now);
                        double score2 = calculatePopularity(r2, username, maxViewCount, maxRatingCount, now);
                        return Double.compare(score2, score1);
                    })
                    .toList();
        }

        return filtered.stream().map(r -> buildRecipeResponseDTO(r, username)).toList();
    }


    private double calculatePopularity(Recipe recipe, String username, double maxViewCount, double maxRatingCount, long now) {
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        if (averageRating == null) averageRating = 0.0;

        int ratingCount = ratingService.getRatingCount(recipe.getId());

        double normalizedViews = (maxViewCount == 0) ? 0 : recipe.getViewCount() / maxViewCount;
        double normalizedRatingCount = (maxRatingCount == 0) ? 0 : ratingCount / maxRatingCount;

        Instant createdAtInstant = recipe.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        long daysSinceCreated = Duration.between(createdAtInstant, Instant.ofEpochMilli(now)).toDays();

        double freshness = Math.max(0, 1 - (daysSinceCreated / 30.0));

        return 0.5 * averageRating + 0.3 * normalizedViews + 0.1 * normalizedRatingCount + 0.1 * freshness;
    }


    private RecipeResponseDTO buildRecipeResponseDTO(Recipe recipe, String username) {
        boolean isFavorite = favoriteService.isFavorite(recipe.getId(), username);
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        String ownerUsername = recipe.getUser().getUsername();
        int ratingCount = ratingService.getRatingCount(recipe.getId());
        return dtoMapper.mapToRecipeResponseDTO(recipe, isFavorite, averageRating, ownerUsername, ratingCount);
    }
}



