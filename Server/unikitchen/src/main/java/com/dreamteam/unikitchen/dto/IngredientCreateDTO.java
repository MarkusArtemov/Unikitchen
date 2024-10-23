package com.dreamteam.unikitchen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class IngredientCreateDTO {
    private String name;
    private Double quantity;
    private String unit;
}
