package ru.practice.recipe_aggregator.user_service.service;

import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface FavoriteRecipeService {
    void add2Favorites(String username, String recipeName);

    void removeFromFavorites(String username, String recipeName);

    List<RecipeResponseDto> getFavorites(String username);
}
