package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public class Mins4CookFilter implements Filter {

    @Override
    public void filter(List<RecipeDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxMinsForCooking() == null || searchContainer.maxMinsForCooking() < 1) {
            searchContainer.maxMinsForCooking(0);
            return;
        }
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than upper limit of mins for cooking""");
        }
        recipes.removeIf(recipe ->
                (int) recipe.minsForCooking().toMinutes() > searchContainer.maxMinsForCooking());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        if (searchContainer.maxTotalMinutes() == null) return true;
        return searchContainer.maxMinsForCooking() <= searchContainer.maxTotalMinutes();
    }

    @Override
    public String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
