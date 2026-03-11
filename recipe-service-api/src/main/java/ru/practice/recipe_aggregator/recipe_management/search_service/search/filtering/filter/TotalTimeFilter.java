package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;

import lombok.extern.slf4j.Slf4j;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@Slf4j
public class TotalTimeFilter implements Filter {

    @Override
    public void filter(List<RecipeDto> recipes, SearchContainer searchContainer) {
        FilterCondition filterCondition = searchContainer.totalTimeCondition();
        if (isInvalidCondition(filterCondition)) {
            log.trace("Filter condition null or value <= 0");
            return;
        }
        long totalMinutes = filterCondition.value();
        recipes.removeIf(recipe -> {
            long totalTime = recipe.totalTime().toMinutes();
            return Filter.isSuitableForRemove(filterCondition, totalMinutes, totalTime);
        });
    }

    private boolean isInvalidCondition(final FilterCondition filterCondition) {
        return filterCondition == null || filterCondition.value() <= 0;
    }


    @Override
    public String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
