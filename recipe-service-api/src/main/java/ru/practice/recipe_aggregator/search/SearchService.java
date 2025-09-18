package ru.practice.recipe_aggregator.search;



import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface SearchService {
    List<RecipeResponseDto> searchByName(SearchContainer container);

    List<RecipeResponseDto> searchByNameWithFiltering(SearchContainer container);

    List<RecipeResponseDto> searchByIngredientsWithFiltering(SearchContainer container);

    List<RecipeResponseDto> searchByIngredients(SearchContainer container);
}
