package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;

import java.util.List;

public class ServingsFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxServings() == null || searchContainer.maxServings() < 0) {
            searchContainer.maxServings(Integer.MAX_VALUE);
        }
        if (searchContainer.minServings() == null) {
            searchContainer.minServings(0);
        }
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    maxServings must be greater than minServings,
                    minServings must be greater than -1
                    """);
        }
        recipes.removeIf(recipe ->
                recipe.servings() > searchContainer.maxServings() || recipe.servings() < searchContainer.minServings());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        int minServings = searchContainer.minServings();
        int maxServings = searchContainer.maxServings();
        return minServings <= maxServings && minServings >= 0;
    }
}
