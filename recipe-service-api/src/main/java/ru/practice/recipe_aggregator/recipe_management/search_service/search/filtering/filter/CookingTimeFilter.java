package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;


import lombok.extern.slf4j.Slf4j;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.List;

@Slf4j
public class CookingTimeFilter implements Filter {


    @Override
    public void filter(List<RecipeDto> recipes, SearchContainer searchContainer) {
        FilterCondition filterCondition = searchContainer.cookingTimeCondition();
        if (isInvalidCondition(filterCondition)) {
            log.trace("Filter condition null or value <= 0");
            return;
        }
        long minutesForCooking = filterCondition.value();
        recipes.removeIf(recipe -> {
            if (recipeHasNotCookingTimeParam(recipe)) {
                return false;
            }

            long cookingTime = recipe.timeForCooking().toMinutes();
            return Filter.isSuitableForRemove(filterCondition, minutesForCooking, cookingTime);
        });
    }

    private boolean isInvalidCondition(final FilterCondition filterCondition) {
        return filterCondition == null || filterCondition.value() <= 0;
    }


    /**
     * Checks if the recipe has a cooking time parameter specified.
     * <p><b>Important:</b> The value {@code Duration.ofMinutes(0)} signifies that
     * the cooking time parameter is missing or not specified for the recipe.</p>
     *
     * @param recipeDto the recipe DTO to check for cooking time parameter
     * @return {@code true} if the recipe has a non-zero cooking time parameter,
     * {@code false} if the cooking time is zero (indicating missing parameter)
     */
    private boolean recipeHasNotCookingTimeParam(final RecipeDto recipeDto) {
        return recipeDto.timeForCooking().equals(Duration.ofMinutes(0));
    }


    @Override
    public String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
