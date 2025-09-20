package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;

public interface RecipeService {
    RecipeResponseDto findRecipeByName(String name);
}
