package ru.practice.search_service.service.filtering.filter;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.exception.InvalidConditionException;

import java.util.List;

public class Mins4PrepFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (searchContainer.maxMins4Prep() <= 0) {
            searchContainer.maxMins4Prep(Integer.MAX_VALUE);
        }
        if (!isValidCondition(searchContainer)) {
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than upper limit of mins for preparing,
                    upper limit of mins for preparing must be greater than 0""");
        }
        recipes.removeIf(recipe ->
                recipe.mins4Prep() > searchContainer.maxMins4Prep());
    }

    private boolean isValidCondition(SearchContainer searchContainer) {
        return searchContainer.maxMins4Prep() <= searchContainer.maxTotalMins();
    }
}
