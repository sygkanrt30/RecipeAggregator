package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;

import lombok.extern.slf4j.Slf4j;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.List;

@Slf4j
public class PreparingTimeFilter implements Filter {

    @Override
    public void filter(List<RecipeDto> recipes, SearchContainer searchContainer) {
        FilterCondition filterCondition = searchContainer.preparationTimeCondition();
        if (isInvalidCondition(filterCondition)) {
            log.trace("Filter condition null or value <= 0");
            return;
        }
        long preparingTime = filterCondition.value();
        recipes.removeIf(recipe -> {
            if (recipeHasNotPreparingTimeParam(recipe)) {
                return false;
            }
            long timeForPreparing = recipe.timeForPreparing().toMinutes();
            return Filter.isSuitableForRemove(filterCondition, preparingTime, timeForPreparing);
        });
    }

    private boolean isInvalidCondition(final FilterCondition filterCondition) {
        return filterCondition == null || filterCondition.value() <= 0;
    }

    /**
     * Checks if the recipe has a preparing time parameter specified.
     * <p><b>Important:</b> The value {@code Duration.ofMinutes(0)} signifies that
     * the preparing time parameter is missing or not specified for the recipe.</p>
     *
     * @param recipeDto the recipe DTO to check for preparing time parameter
     * @return {@code true} if the recipe has a non-zero preparing time parameter,
     * {@code false} if the cooking time is zero (indicating missing parameter)
     */
    private boolean recipeHasNotPreparingTimeParam(final RecipeDto recipeDto) {
        return recipeDto.timeForPreparing().equals(Duration.ofMinutes(0));
    }

    @Override
    public String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
