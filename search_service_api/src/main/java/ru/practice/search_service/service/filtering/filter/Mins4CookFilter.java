package ru.practice.search_service.service.filtering.filter;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.exception.InvalidConditionException;

import java.util.List;

public class Mins4CookFilter implements Filter {
    @Override
    public void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        if (isValidConditionOrThrow(searchContainer)) {
            recipes.removeIf(recipe ->
                    recipe.mins4Cook() > searchContainer.maxMins4Cook());
        }
    }

    private boolean isValidConditionOrThrow(SearchContainer searchContainer) {
        int maxMins4Cook = searchContainer.maxMins4Cook();
        if (maxMins4Cook < 1 || maxMins4Cook > searchContainer.maxTotalMins()){
            throw new InvalidConditionException("""
                    Invalid condition:
                    total mins must be greater than upper limit of mins for cooking,
                    upper limit of mins for cooking must be greater than 0""");
        }
        return true;
    }
}
