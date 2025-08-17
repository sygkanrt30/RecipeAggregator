package ru.practice.search_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.FilterService;
import ru.practice.search_service.service.search.SearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {
    private final SearchService searchService;
    private final FilterService filterService;

    @GetMapping("/search-by-name/{name}")
    public List<RecipeResponseDto> searchByName(@PathVariable String name) {
        var container = SearchContainer.builder()
                .name(name)
                .build();
        return searchService.searchByName(container);
    }

    @GetMapping("/search-by-ingredients")
    public List<RecipeResponseDto> searchByIngredients(@RequestBody List<String> ingredientsName) {
        var container = SearchContainer.builder()
                .ingredientsName(ingredientsName)
                .build();
        return searchService.searchByIngredients(container);
    }

    @GetMapping("/filter")
    public List<RecipeResponseDto> filter(
            @RequestBody List<RecipeResponseDto> recipes,
            @RequestParam(required = false) int maxMins4Cook,
            @RequestParam(required = false) int maxTotalMins,
            @RequestParam(required = false) int maxMins4Prep,
            @RequestParam(required = false) int minServings,
            @RequestParam(required = false) int maxServings) {
        var container = SearchContainer.builder()
                .maxMins4Cook(maxMins4Cook)
                .maxTotalMins(maxTotalMins)
                .maxMins4Prep(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return filterService.processWithFilterChain(recipes, container);
    }

    @GetMapping("/search-by-ingredients-with-filtering")
    public List<RecipeResponseDto> searchByIngredientsWithFiltering(
            @RequestParam List<String> ingredientsName,
            @RequestParam(required = false) int maxMins4Cook,
            @RequestParam(required = false) int maxTotalMins,
            @RequestParam(required = false) int maxMins4Prep,
            @RequestParam(required = false) int minServings,
            @RequestParam(required = false) int maxServings
    ) {
        var container = SearchContainer.builder()
                .ingredientsName(ingredientsName)
                .maxMins4Cook(maxMins4Cook)
                .maxTotalMins(maxTotalMins)
                .maxMins4Prep(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return searchService.searchByIngredientsWithFiltering(container);
    }

    @GetMapping("/search-by-name-with-filtering")
    public List<RecipeResponseDto> searchByNameWithFiltering(
            @RequestParam String name,
            @RequestParam(required = false) int maxMins4Cook,
            @RequestParam(required = false) int maxTotalMins,
            @RequestParam(required = false) int maxMins4Prep,
            @RequestParam(required = false) int minServings,
            @RequestParam(required = false) int maxServings
    ) {
        var container = SearchContainer.builder()
                .name(name)
                .maxMins4Cook(maxMins4Cook)
                .maxTotalMins(maxTotalMins)
                .maxMins4Prep(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return searchService.searchByNameWithFiltering(container);
    }
}
