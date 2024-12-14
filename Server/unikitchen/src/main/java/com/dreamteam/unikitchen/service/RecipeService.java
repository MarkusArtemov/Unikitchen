package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.RecipeCreateDTO;
import com.dreamteam.unikitchen.dto.RecipeResponseDTO;
import com.dreamteam.unikitchen.dto.RecipeUpdateDTO;
import com.dreamteam.unikitchen.mapper.DTOMapper;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.FavoriteRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.dreamteam.unikitchen.repository.RatingRepository;

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
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, DTOMapper dtoMapper,
                         RatingService ratingService, FavoriteService favoriteService,
                         UserService userService, ImageService imageService, FavoriteRepository favoriteRepository, RatingRepository ratingRepository) {
        this.recipeRepository = recipeRepository;
        this.dtoMapper = dtoMapper;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.imageService = imageService;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
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

    @Transactional
    public void deleteRecipe(Long recipeId, String username) {
        // Überprüfung, ob das Rezept existiert und der aktuelle Nutzer der Besitzer ist
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        // Löschen aller Bewertungen für das Rezept
        ratingRepository.deleteByRecipeId(recipeId);

        // Löschen aller Favoriten für das Rezept
        favoriteRepository.deleteByRecipeId(recipeId);

        // Löschen des Rezeptbildes, falls vorhanden
        if (recipe.getRecipeImagePath() != null) {
            try {
                imageService.deleteImage(recipe.getRecipeImagePath());
            } catch (Exception e) {
                // Fehler beim Löschen des Bildes, aber der Löschprozess geht weiter
                System.err.println("Fehler beim Löschen des Bildes: " + e.getMessage());
            }
        }

        // Löschen des Rezepts
        recipeRepository.delete(recipe);
    }

    public Page<RecipeResponseDTO> getAllRecipes(String username, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        return recipes.map(recipe -> buildRecipeResponseDTO(recipe, username));
    }

    public Page<RecipeResponseDTO> getAllRecipesByUsername(String username, Pageable pageable) {
        User user = userService.getUserEntityByUsername(username);
        Page<Recipe> recipes = recipeRepository.findByUserId(user.getId(), pageable);
        return recipes.map(recipe -> buildRecipeResponseDTO(recipe, username));
    }

    public RecipeResponseDTO getRecipeById(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (username != null && !recipe.getUser().getUsername().equals(username)) {
            recipe.setViewCount(recipe.getViewCount() + 1);
            recipeRepository.save(recipe);
        }

        return buildRecipeResponseDTO(recipe, username);
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

    public List<RecipeResponseDTO> getLast10Recipes(String username) {
        List<Recipe> last10Recipes = recipeRepository.findTop10ByOrderByCreatedAtDesc();
        return last10Recipes.stream()
                .map(recipe -> buildRecipeResponseDTO(recipe, username))
                .toList();
    }

    public Page<RecipeResponseDTO> getFilteredRecipes(String category,
                                                      Boolean cheap,
                                                      Boolean quick,
                                                      String difficultyLevel,
                                                      String sortBy,
                                                      String direction,
                                                      int page,
                                                      int size,
                                                      String username) {
        Integer maxPrice = (Boolean.TRUE.equals(cheap)) ? 10 : null;
        Integer maxDuration = (Boolean.TRUE.equals(quick)) ? 15 : null;


        boolean sortByPopular = "popular".equalsIgnoreCase(sortBy);

        if (sortByPopular) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Recipe> filteredRecipes = recipeRepository.findByFilters(maxDuration, difficultyLevel, category, maxPrice, pageable);


            long now = System.currentTimeMillis();
            double maxViewCount = filteredRecipes.stream().mapToDouble(Recipe::getViewCount).max().orElse(1);
            double maxRatingCount = filteredRecipes.stream().mapToDouble(r -> ratingService.getRatingCount(r.getId())).max().orElse(1);

            List<Recipe> sortedByPopularity = filteredRecipes.getContent().stream()
                    .sorted((r1, r2) -> {
                        double score1 = calculatePopularity(r1, username, maxViewCount, maxRatingCount, now);
                        double score2 = calculatePopularity(r2, username, maxViewCount, maxRatingCount, now);
                        return Double.compare(score2, score1);
                    })
                    .toList();

            Page<Recipe> resultPage = new PageImpl<>(sortedByPopularity, pageable, filteredRecipes.getTotalElements());
            return resultPage.map(r -> buildRecipeResponseDTO(r, username));

        } else {
            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = "createdAt";
            }

            Sort sort = (direction != null && direction.equalsIgnoreCase("ASC"))
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Recipe> filteredRecipes = recipeRepository.findByFilters(maxDuration, difficultyLevel, category, maxPrice, pageable);

            return filteredRecipes.map(r -> buildRecipeResponseDTO(r, username));
        }
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
        boolean isFavorite = false;
        if (username != null && !username.isEmpty()) {
            isFavorite = favoriteService.isFavorite(recipe.getId(), username);
        }
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        String ownerUsername = recipe.getUser().getUsername();
        int ratingCount = ratingService.getRatingCount(recipe.getId());
        return dtoMapper.mapToRecipeResponseDTO(recipe, isFavorite, averageRating, ownerUsername, ratingCount);
    }
}
