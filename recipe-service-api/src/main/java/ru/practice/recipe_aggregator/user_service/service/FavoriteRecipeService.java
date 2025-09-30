package ru.practice.recipe_aggregator.user_service.service;

import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;

import java.util.List;
import java.util.UUID;

public interface FavoriteRecipeService {
    void add2Favorites(String username, UUID recipeId);

    void removeFromFavorites(String username, UUID recipeId);

    List<RecipeResponseDto> getFavorites(String username);
}
