package ru.practice.search_service.service.filtering.filter;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.exception.InvalidConditionException;

import java.util.List;

public class TotalMinsFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxTotalMins() <= 0) {
            searchContainer.maxTotalMins(Integer.MAX_VALUE);
        }
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
        int totalMins = searchContainer.maxTotalMins();
        return totalMins >= (searchContainer.maxMins4Cook() + searchContainer.maxMins4Prep());
    }
}
