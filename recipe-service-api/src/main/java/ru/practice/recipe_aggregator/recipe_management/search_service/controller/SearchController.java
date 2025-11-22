package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RequestMapper;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.SearchService;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {

    private final SearchService searchService;
    private final RequestMapper requestMapper;

    @GetMapping("/name/{name}")
    public List<RecipeDto> searchByName(@PathVariable @Valid @NotBlank String name) {
        return searchService.searchByName(name);
    }

    @PostMapping("/ingredients")
    public List<RecipeDto> searchByIngredients(@RequestBody @Valid @NotEmpty Set<String> ingredientNames) {
        return searchService.searchByIngredients(ingredientNames);
    }

    @PostMapping("/with-filtering")
    public List<RecipeDto> searchByIngredientsWithFiltering(@RequestBody SearchRequest request) {
        var container = requestMapper.toSearchContainer(request);
        return searchService.searchWithFiltering(container);
    }
}
