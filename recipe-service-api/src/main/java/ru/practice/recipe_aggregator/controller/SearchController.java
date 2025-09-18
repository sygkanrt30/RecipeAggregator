package ru.practice.recipe_aggregator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.search.SearchService;
import ru.practice.recipe_aggregator.search.filtering.FilterService;


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
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings) {
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
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings
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
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings
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
