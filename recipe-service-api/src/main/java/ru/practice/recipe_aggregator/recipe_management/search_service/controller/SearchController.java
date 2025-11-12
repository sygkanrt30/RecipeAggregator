package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.SearchService;
import ru.practice.shared.dto.RecipeDto;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/name/{name}")
    public List<RecipeDto> searchByName(@PathVariable @NotBlank String name) {
        var container = SearchContainer.builder()
                .name(name)
                .build();
        return searchService.searchByName(container);
    }

    @GetMapping("/ingredients")
    public List<RecipeDto> searchByIngredients(@RequestBody Set<String> ingredientNames) {
        var container = SearchContainer.builder()
                .ingredientNames(ingredientNames)
                .build();
        return searchService.searchByIngredients(container);
    }

    @GetMapping("/ingredients-with-filtering")
    public List<RecipeDto> searchByIngredientsWithFiltering(
            @RequestParam Set<String> ingredientsName,
            @RequestParam(required = false) Integer maxMins4Cook,
            @RequestParam(required = false) Integer maxTotalMins,
            @RequestParam(required = false) Integer maxMins4Prep,
            @RequestParam(required = false) Integer minServings,
            @RequestParam(required = false) Integer maxServings
    ) {
        var container = SearchContainer.builder()
                .ingredientNames(ingredientsName)
                .maxMinsForCooking(maxMins4Cook)
                .maxTotalMinutes(maxTotalMins)
                .maxMinsForPreparing(maxMins4Prep)
                .minServings(minServings)
                .maxServings(maxServings)
                .build();
        return searchService.searchByIngredientsWithFiltering(container);
    }

    @GetMapping("/name-with-filtering")
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
