package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;

import java.util.List;

public class TotalMinsFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxTotalMins() == null || searchContainer.maxTotalMins() < 1) return;
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than sum of max mins for cooking and for preparing
                    """);
        }
        recipes.removeIf(recipe ->
                recipe.totalMins() > searchContainer.maxTotalMins());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        if (searchContainer.maxMins4Prep() == null || searchContainer.maxMins4Cook() == null) return true;
        int totalMins = searchContainer.maxTotalMins();
        return totalMins >= (searchContainer.maxMins4Cook() + searchContainer.maxMins4Prep());
    }
}
