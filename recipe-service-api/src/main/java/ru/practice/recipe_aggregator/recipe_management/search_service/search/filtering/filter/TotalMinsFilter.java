package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public class TotalMinsFilter implements Filter {
    @Override
    public void filter(List<RecipeDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxTotalMinutes() == null || searchContainer.maxTotalMinutes() < 1) return;
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than sum of max mins for cooking and for preparing
                    """);
        }
        recipes.removeIf(recipe ->
                (int) recipe.totalMins().toMinutes() > searchContainer.maxTotalMinutes());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        if (searchContainer.maxMinsForPreparing() == null || searchContainer.maxMinsForCooking() == null) return true;
        int totalMins = searchContainer.maxTotalMinutes();
        return totalMins >= (searchContainer.maxMinsForCooking() + searchContainer.maxMinsForPreparing());
    }

    @Override
    public String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
