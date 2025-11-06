package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.SearchService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {
    private final SearchService searchService;
    private final FilterService filterService;

    @GetMapping("/search-by-name/{name}")
    public List<RecipeDto> searchByName(@PathVariable String name) {
        var container = SearchContainer.builder()
                .name(name)
                .build();
        return searchService.searchByName(container);
    }

    @GetMapping("/search-by-ingredients")
    public List<RecipeDto> searchByIngredients(@RequestBody List<String> ingredientsName) {
        var container = SearchContainer.builder()
                .ingredientsName(ingredientsName)
                .build();
        return searchService.searchByIngredients(container);
    }

    @GetMapping("/filter")
    public List<RecipeDto> filter(
            @RequestBody List<RecipeDto> recipes,
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings) {
        var container = SearchContainer.builder()
                .maxMinsForCooking(maxMins4Cook)
                .maxTotalMinutes(maxTotalMins)
                .maxMinsForPreparing(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return filterService.processWithFilterChain(recipes, container);
    }

    @GetMapping("/search-by-ingredients-with-filtering")
    public List<RecipeDto> searchByIngredientsWithFiltering(
            @RequestParam List<String> ingredientsName,
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings
    ) {
        var container = SearchContainer.builder()
                .ingredientsName(ingredientsName)
                .maxMinsForCooking(maxMins4Cook)
                .maxTotalMinutes(maxTotalMins)
                .maxMinsForPreparing(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return searchService.searchByIngredientsWithFiltering(container);
    }

    @GetMapping("/search-by-name-with-filtering")
    public List<RecipeDto> searchByNameWithFiltering(
            @RequestParam String name,
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings
    ) {
        var container = SearchContainer.builder()
                .name(name)
                .maxMinsForCooking(maxMins4Cook)
                .maxTotalMinutes(maxTotalMins)
                .maxMinsForPreparing(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return searchService.searchByNameWithFiltering(container);
    }
}
