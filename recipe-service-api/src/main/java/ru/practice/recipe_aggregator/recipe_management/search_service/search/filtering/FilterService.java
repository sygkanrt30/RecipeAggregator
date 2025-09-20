package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface FilterService {
    List<RecipeResponseDto> processWithFilterChain(List<RecipeResponseDto> recipes, SearchContainer searchContainer);
}
