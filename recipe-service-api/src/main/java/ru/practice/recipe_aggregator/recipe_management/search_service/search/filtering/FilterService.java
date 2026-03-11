package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface FilterService {

    List<RecipeDto> processWithFilterChain(List<RecipeDto> recipes, SearchContainer searchContainer);
}
