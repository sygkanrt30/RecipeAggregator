package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;

public interface SearchService {

    List<RecipeDto> searchByName(String name);

    List<RecipeDto> searchByNameWithFiltering(SearchContainer container);

    List<RecipeDto> searchByIngredientsWithFiltering(SearchContainer container);

    List<RecipeDto> searchByIngredients(Set<String> ingredientNames);
}
