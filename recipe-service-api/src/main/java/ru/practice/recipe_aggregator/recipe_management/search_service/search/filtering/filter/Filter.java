package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface Filter {

    void filter(List<RecipeDto> recipes, SearchContainer searchContainer);

    String getFilterName();

    static boolean isSuitableForRemove(FilterCondition filterCondition, long conditionValue, long recipeValue) {
        return switch (filterCondition.operator()) {
            case EQ -> recipeValue != conditionValue;
            case NEQ -> recipeValue == conditionValue;
            case GT -> recipeValue <= conditionValue;
            case LT -> recipeValue >= conditionValue;
            case GTE -> recipeValue < conditionValue;
            case LTE -> recipeValue > conditionValue;
        };
    }
}
