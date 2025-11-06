package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public class Mins4PrepFilter implements Filter {
    @Override
    public void filter(List<RecipeDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxMinsForPreparing() == null || searchContainer.maxMinsForPreparing() < 1) {
            searchContainer.maxMinsForPreparing(0);
            return;
        }
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than upper limit of mins for preparing""");
        }
        recipes.removeIf(recipe ->
                (int) recipe.minsForPreparing().toMinutes() > searchContainer.maxMinsForPreparing());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        if (searchContainer.maxTotalMinutes() == null) return true;
        return searchContainer.maxMinsForPreparing() <= searchContainer.maxTotalMinutes();
    }

    @Override
    public String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
