package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface Filter {
    void filter(List<RecipeDto> recipes, SearchContainer searchContainer);

    String getFilterName();
}
