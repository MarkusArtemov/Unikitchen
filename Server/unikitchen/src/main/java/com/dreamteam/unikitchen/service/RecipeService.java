package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.context.CurrentUserContext;
import com.dreamteam.unikitchen.dto.RecipeCreationRequest;
import com.dreamteam.unikitchen.dto.RecipeDetailsResponse;
import com.dreamteam.unikitchen.dto.RecipeFilterRequest;
import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.dto.RecipeUpdateRequest;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
public class RecipeService {

    private static final double W_RATING = 0.6;
    private static final double W_VIEWS = 0.2;
    private static final double W_FRESHNESS = 0.2;
    private static final double FRESHNESS_DECAY_DAYS = 30.0;

    private final RecipeRepository recipeRepository;
    private final EntityMapper entityMapper;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, EntityMapper entityMapper,
                         RatingService ratingService, FavoriteService favoriteService,
                         UserService userService, ImageService imageService) {
        this.recipeRepository = recipeRepository;
        this.entityMapper = entityMapper;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.imageService = imageService;
    }

    public RecipeDetailsResponse createRecipe(RecipeCreationRequest recipeCreationRequest) {
        User user = userService.getUserEntity();
        Recipe recipe = entityMapper.fromRecipeCreationRequestToRecipe(recipeCreationRequest, user);
        validateRecipe(recipe);
        recipeRepository.save(recipe);
        return buildRecipeDetailsResponse(recipe);
    }

    public RecipeDetailsResponse updateRecipe(Long recipeId, RecipeUpdateRequest updatedRecipe) {
        String username = CurrentUserContext.getCurrentUsername();
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!existingRecipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        existingRecipe = entityMapper.fromRecipeUpdateRequestToRecipe(updatedRecipe, existingRecipe.getUser());
        validateRecipe(existingRecipe);
        recipeRepository.save(existingRecipe);
        return buildRecipeDetailsResponse(existingRecipe);
    }

    public void deleteRecipe(Long recipeId) {
        String username = CurrentUserContext.getCurrentUsername();
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        if (!recipe.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not the owner of this recipe");
        }

        favoriteService.deleteFavoritesByRecipeId(recipeId);
        ratingService.deleteRatingsByRecipeId(recipeId);
        imageService.deleteImage(recipe.getRecipeImagePath());
        recipeRepository.delete(recipe);
    }

    public List<RecipeDetailsResponse> getAllRecipesByUsername() {
        User user = userService.getUserEntity();
        return recipeRepository.findByUserId(user.getId()).stream()
                .map(this::buildRecipeDetailsResponse)
                .toList();
    }

    public RecipeDetailsResponse getRecipeById(Long recipeId) {
        String username = CurrentUserContext.getCurrentUsername();
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(username)) {
            recipe.setViewCount(recipe.getViewCount() + 1);
            recipeRepository.save(recipe);
        }

        return buildRecipeDetailsResponse(recipe);
    }

    public List<RecipeOverviewResponse> getLast10Recipes() {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::buildRecipeOverviewResponse)
                .toList();
    }

    public void uploadRecipeImage(Long recipeId, MultipartFile image) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        String currentUsername = CurrentUserContext.getCurrentUsername();
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

    public Page<RecipeOverviewResponse> getFilteredRecipes(RecipeFilterRequest filter) {
        Integer maxPrice = Boolean.TRUE.equals(filter.cheap()) ? 10 : null;
        Integer maxDuration = Boolean.TRUE.equals(filter.quick()) ? 30 : null;
        Category categoryEnum = parseCategory(filter.category());

        if (!"popular".equalsIgnoreCase(filter.sortBy())) {
            Pageable pageable = getPageable(filter.sortBy(), filter.direction(), filter.page(), filter.size());
            Page<Recipe> filteredRecipes = recipeRepository.findByFilters(
                    maxDuration, filter.difficultyLevel(), categoryEnum, maxPrice, pageable);
            return filteredRecipes.map(this::buildRecipeOverviewResponse);
        }

        // Beliebtheitssortierung
        Page<Recipe> filteredPage = recipeRepository.findByFilters(
                maxDuration, filter.difficultyLevel(), categoryEnum, maxPrice, Pageable.unpaged()
        );

        List<Recipe> filteredList = filteredPage.getContent();
        return sortByPopularity(filteredList, filter.page(), filter.size(), filter.direction());
    }

    private Category parseCategory(String category) {
        if (category != null && !category.isEmpty()) {
            try {
                return Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ungültige Kategorie: " + category);
            }
        }
        return null;
    }

    private Pageable getPageable(String sortBy, String direction, int page, int size) {
        Sort sort = (direction != null && direction.equalsIgnoreCase("ASC"))
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private Page<RecipeOverviewResponse> sortByPopularity(List<Recipe> recipes, int page, int size, String direction) {
        if (recipes.isEmpty()) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        }

        double maxViewCount = recipes.stream()
                .mapToDouble(Recipe::getViewCount)
                .max().orElse(0.0);

        double maxRatingCount = recipes.stream()
                .mapToDouble(r -> ratingService.getRatingCount(r.getId()))
                .max().orElse(0.0);

        long now = System.currentTimeMillis();

        List<RecipePopularityWrapper> wrapped = recipes.stream()
                .map(recipe -> new RecipePopularityWrapper(
                        recipe,
                        calculatePopularity(recipe, maxViewCount, maxRatingCount, now))
                )
                .toList();

        Comparator<RecipePopularityWrapper> comparator = Comparator.comparingDouble(r -> r.popularity);
        if (direction == null || !direction.equalsIgnoreCase("ASC")) {
            comparator = comparator.reversed();
        }

        List<RecipePopularityWrapper> sorted = wrapped.stream()
                .sorted(comparator)
                .toList();

        int start = Math.min(page * size, sorted.size());
        int end = Math.min((page + 1) * size, sorted.size());
        List<RecipeOverviewResponse> result = sorted.subList(start, end).stream()
                .map(wrapper -> buildRecipeOverviewResponse(wrapper.recipe))
                .toList();

        return new PageImpl<>(result, PageRequest.of(page, size), sorted.size());
    }

    private double calculatePopularity(Recipe recipe, double maxViewCount, double maxRatingCount, long now) {
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        if (averageRating == null) averageRating = 0.0;

        int ratingCount = ratingService.getRatingCount(recipe.getId());
        double normalizedViews = (maxViewCount == 0) ? 0.0 : recipe.getViewCount() / maxViewCount;
        double normalizedRatingCount = (maxRatingCount == 0) ? 0.0 : ratingCount / maxRatingCount;

        // Rating Score (kombiniert Durchschnitsbewertung und Anzahl)
        // Erhöht Relevanz von häufig und gut bewerteten Rezepten.
        double ratingScore = averageRating * normalizedRatingCount;

        Instant createdAtInstant = recipe.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        long daysSinceCreated = Duration.between(createdAtInstant, Instant.ofEpochMilli(now)).toDays();
        double freshness = Math.max(0.0, 1.0 - (daysSinceCreated / FRESHNESS_DECAY_DAYS));

        // Gesamt-Popularität zusammenrechnen
        return (ratingScore * W_RATING) + (normalizedViews * W_VIEWS) + (freshness * W_FRESHNESS);
    }

    private record RecipePopularityWrapper(Recipe recipe, double popularity) {}

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

    public RecipeDetailsResponse buildRecipeDetailsResponse(Recipe recipe) {
        boolean isFavorite = favoriteService.isFavorite(recipe.getId());
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        int ratingCount = ratingService.getRatingCount(recipe.getId());
        String ownerUsername = recipe.getUser().getUsername();
        return entityMapper.toRecipeDetailsResponse(recipe, isFavorite, averageRating, ratingCount, ownerUsername);
    }

    public RecipeOverviewResponse buildRecipeOverviewResponse(Recipe recipe) {
        String username = CurrentUserContext.getCurrentUsername();
        boolean isFavorite = username != null && favoriteService.isFavorite(recipe.getId());
        Double averageRating = ratingService.calculateAverageRating(recipe.getId());
        int ratingCount = ratingService.getRatingCount(recipe.getId());
        return entityMapper.toRecipeOverviewResponse(recipe, isFavorite, averageRating, ratingCount);
    }
}
