package ru.practice.search_service.service.filtering.filter;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.exception.InvalidConditionException;

import java.util.List;

public class ServingsFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (isValidConditionOrThrow(searchContainer)) {
            recipes.removeIf(recipe ->
                    recipe.servings() > searchContainer.maxServings() || recipe.servings() < searchContainer.minServings());
        }
    }

    private boolean isValidConditionOrThrow(SearchContainer searchContainer) {
        int minServings = searchContainer.minServings();
        int maxServings = searchContainer.maxServings();
        if (minServings >= maxServings || maxServings < 1 || minServings < 0) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    maxServings must be greater than minServings,
                    maxServings must be greater than 0,
                    minServings must be greater than -1
                    """);
        }
        return true;
    }
}
