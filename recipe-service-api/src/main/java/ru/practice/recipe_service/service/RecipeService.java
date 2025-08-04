package ru.practice.recipe_service.service;

import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;

public interface RecipeService {
    RecipeResponseDto findRecipe(String name);

    void deleteRecipe(String username);
}
