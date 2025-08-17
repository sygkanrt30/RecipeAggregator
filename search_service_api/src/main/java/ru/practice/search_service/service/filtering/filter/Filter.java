package ru.practice.search_service.service.filtering.filter;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;

import java.util.List;

public interface Filter {
    void filter(List<RecipeResponseDto> recipes, SearchContainer searchContainer);
}
