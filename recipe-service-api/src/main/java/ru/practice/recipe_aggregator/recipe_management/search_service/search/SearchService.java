package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface SearchService {

    List<RecipeDto> searchByName(SearchContainer container);

    List<RecipeDto> searchByNameWithFiltering(SearchContainer container);

    List<RecipeDto> searchByIngredientsWithFiltering(SearchContainer container);

    List<RecipeDto> searchByIngredients(SearchContainer container);
}
