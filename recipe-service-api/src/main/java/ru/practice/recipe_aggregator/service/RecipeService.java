package ru.practice.recipe_aggregator.service;

import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;

public interface RecipeService {
    RecipeResponseDto findRecipeByName(String name);
}
