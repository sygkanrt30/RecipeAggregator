package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RequestMapper;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.SearchService;
import ru.practice.recipe_aggregator.translator.TranslatorUtil;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {

    private final SearchService searchService;
    private final RequestMapper requestMapper;
    private final TranslatorUtil translator;

    @GetMapping("/name/{name}")
    public List<RecipeDto> searchByName(@PathVariable @Valid @NotBlank String name) {
        String nameOnEN = translator.translateTextDependingOnWebsiteLanguage(name);
        List<RecipeDto> resultOnEN = searchService.searchByName(nameOnEN);
        return translator.translateDtoDependingOnWebsiteLanguage(resultOnEN);
    }

    @PostMapping("/ingredients")
    public List<RecipeDto> searchByIngredients(@RequestBody @Valid @NotEmpty Set<String> ingredientNames) {
        var ingredientsOnEN = ingredientNames.stream()
                .map(translator::translateTextDependingOnWebsiteLanguage).
                collect(Collectors.toSet());
        List<RecipeDto> resultOnEN = searchService.searchByIngredients(ingredientsOnEN);
        return translator.translateDtoDependingOnWebsiteLanguage(resultOnEN);
    }

    @PostMapping("/with-filtering")
    public List<RecipeDto> searchByIngredientsWithFiltering(@RequestBody SearchRequest request) {
        translateRequestParam(request);
        var container = requestMapper.toSearchContainer(request);
        List<RecipeDto> resultOnEN = searchService.searchWithFiltering(container);
        return translator.translateDtoDependingOnWebsiteLanguage(resultOnEN);
    }

    private void translateRequestParam(SearchRequest request) {
        String nameOnEN = translator.translateTextDependingOnWebsiteLanguage(request.name());
        var ingredientsOnEN = request.ingredientNames().stream()
                .map(translator::translateTextDependingOnWebsiteLanguage).
                collect(Collectors.toSet());
        request.name(nameOnEN);
        request.ingredientNames(ingredientsOnEN);
    }
}
