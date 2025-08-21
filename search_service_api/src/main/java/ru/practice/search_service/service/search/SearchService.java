package ru.practice.search_service.service.search;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface SearchService {
    List<RecipeResponseDto> searchByName(SearchContainer container);

    List<RecipeResponseDto> searchByNameWithFiltering(SearchContainer container);

    List<RecipeResponseDto> searchByIngredientsWithFiltering(SearchContainer container);

    List<RecipeResponseDto> searchByIngredients(SearchContainer container);
}
