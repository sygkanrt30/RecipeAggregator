package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    RecipeResponseDto findRecipeByName(String name);

    List<RecipeResponseDto> findAllByIds(List<UUID> recipeIds);

    UUID getIdByName(String recipeName);
}
