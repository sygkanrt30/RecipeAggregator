package ru.practice.recipe_aggregator.search.filtering;


import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface FilterService {
    List<RecipeResponseDto> processWithFilterChain(List<RecipeResponseDto> recipes, SearchContainer searchContainer);
}
