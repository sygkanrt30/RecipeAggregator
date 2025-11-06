package ru.practice.recipe_aggregator.user_service.service;


import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface FavoriteRecipeService {
    void add2Favorites(String username, String recipeName);

    void removeFromFavorites(String username, String recipeName);

    List<RecipeDto> getFavorites(String username);
}
