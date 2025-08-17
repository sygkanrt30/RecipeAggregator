package ru.practice.search_service.service.filtering;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface FilterService {
    List<RecipeResponseDto> processWithFilterChain(List<RecipeResponseDto> recipes, SearchContainer searchContainer);
}
