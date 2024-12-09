package com.dreamteam.unikitchen.mapper;

import com.dreamteam.unikitchen.dto.*;
import com.dreamteam.unikitchen.model.*;
import com.dreamteam.unikitchen.repository.RatingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DTOMapper {

    private final RatingRepository ratingRepository;

    public DTOMapper(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public FavoriteDTO createFavoriteDTO(Favorite favorite) {
        Recipe recipe = favorite.getRecipe();

        Double averageRating = ratingRepository.findByRecipeId(recipe.getId())
                .stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);

        int ratingCount = ratingRepository.countByRecipeId(recipe.getId());

        return new FavoriteDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getPrice(),
                recipe.getDuration(),
                recipe.getDifficultyLevel(),
                recipe.getCategory(),
                recipe.getRecipeImagePath(),
                averageRating,
                ratingCount
        );
    }

    public IngredientResponseDTO createIngredientResponseDTO(Ingredient ingredient) {
        return new IngredientResponseDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnit(),
                ingredient.getRecipe().getId()
        );
    }

    public RatingDTO createRatingDTO(Rating rating) {
        return new RatingDTO(
                rating.getId(),
                rating.getRatingValue(),
                rating.getUser().getId(),
                rating.getRecipe().getId(),
                rating.getCreatedAt()
        );
    }

    public UserInfoDTO createUserInfoDTO(User user) {
        return new UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public Recipe mapToRecipeEntity(RecipeCreateDTO recipeCreateDTO, User user) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeCreateDTO.name());
        recipe.setPrice(recipeCreateDTO.price());
        recipe.setDuration(recipeCreateDTO.duration());
        recipe.setDifficultyLevel(recipeCreateDTO.difficultyLevel());
        recipe.setPreparation(recipeCreateDTO.preparation());
        recipe.setCategory(recipeCreateDTO.category());
        recipe.setUser(user);
        recipe.setIngredients(mapToIngredientList(recipeCreateDTO.ingredients(), recipe));
        return recipe;
    }

    public List<Ingredient> mapToIngredientList(List<IngredientCreateDTO> ingredientDTOs, Recipe recipe) {
        if (ingredientDTOs == null) {
            return List.of();
        }
        return ingredientDTOs.stream()
                .map(dto -> {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(dto.name());
                    ingredient.setQuantity(dto.quantity());
                    ingredient.setUnit(dto.unit());
                    ingredient.setRecipe(recipe);
                    return ingredient;
                })
                .toList();
    }

    private List<IngredientCreateDTO> mapToIngredientCreateDTO(Recipe recipe) {
        return (recipe.getIngredients() != null)
                ? recipe.getIngredients().stream()
                .map(ingredient -> new IngredientCreateDTO(
                        ingredient.getName(),
                        ingredient.getQuantity(),
                        ingredient.getUnit()))
                .toList()
                : List.of();
    }

    public RecipeResponseDTO mapToRecipeResponseDTO(Recipe recipe, boolean isFavorite, Double averageRating, String ownerUsername, int ratingCount) {
        var ingredients = mapToIngredientCreateDTO(recipe);
        return new RecipeResponseDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getPrice(),
                recipe.getDuration(),
                recipe.getDifficultyLevel(),
                recipe.getCategory(),
                recipe.getPreparation(),
                ingredients,
                averageRating,
                isFavorite,
                recipe.getViewCount(),
                ownerUsername,
                ratingCount
        );
    }


}

