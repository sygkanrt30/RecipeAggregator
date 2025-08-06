package ru.practice.recipe_service.service;

import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface RecipeService {
    RecipeResponseDto findRecipeByName(String name);

    void deleteRecipe(String username);

    List<RecipeResponseDto> findRecipeByIds(List<Long> ids);
}
