package com.dreamteam.unikitchen.dto;

import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {
    private String name;
    private Double price;
    private Integer duration;
    private DifficultyLevel difficultyLevel;
    private Category category;
    private String preparation;
    private List<IngredientCreateDTO> ingredients;
}
