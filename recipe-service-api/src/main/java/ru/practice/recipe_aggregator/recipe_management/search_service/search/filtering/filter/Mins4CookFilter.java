package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;

import java.util.List;

public class Mins4CookFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxMins4Cook() == null || searchContainer.maxMins4Cook() < 1) {
            searchContainer.maxMins4Cook(0);
            return;
        }
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than upper limit of mins for cooking""");
        }
        recipes.removeIf(recipe ->
                recipe.mins4Cook() > searchContainer.maxMins4Cook());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        if (searchContainer.maxTotalMins() == null) return true;
        return searchContainer.maxMins4Cook() <= searchContainer.maxTotalMins();
    }
}
