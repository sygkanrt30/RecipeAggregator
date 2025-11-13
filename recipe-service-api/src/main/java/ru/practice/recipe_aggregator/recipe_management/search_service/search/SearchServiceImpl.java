package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.IngredientsSearcher;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.NameSearcher;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FilterService filterService;
    private final RecipeMapper recipeMapper;
    private final NameSearcher nameSearcher;
    private final IngredientsSearcher ingredientsSearcher;


    @Override
    public List<RecipeDto> searchByName(String name) {
        List<RecipeDoc> recipeDocs = nameSearcher.search(name);
        return convertToRecipeResponseDtoList(recipeDocs);
    }

    private List<RecipeDto> convertToRecipeResponseDtoList(List<RecipeDoc> recipeDocs) {
        return recipeDocs.stream()
                .map(recipeMapper::toRecipeDto)
                .peek(recipeDto -> log.trace(recipeDto.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDto> searchByNameWithFiltering(SearchContainer container) {
        List<RecipeDto> recipes = searchByName(container.name());
        if (recipes.isEmpty()) {
            log.debug("No recipes found for name {}", container.name());
            return recipes;
        }
        filterService.processWithFilterChain(recipes, container);
        return recipes;
    }

    @Override
    public List<RecipeDto> searchByIngredientsWithFiltering(SearchContainer container) {
        List<RecipeDto> recipes = searchByIngredients(container.ingredientNames());
        if (recipes.isEmpty()) {
            log.debug("No recipes found for ingredients {}", container.ingredientNames());
            return recipes;
        }
        filterService.processWithFilterChain(recipes, container);
        return recipes;
    }

    @Override
    public List<RecipeDto> searchByIngredients(Set<String> ingredientNames) {
        List<RecipeDoc> recipeDocs = ingredientsSearcher.search(ingredientNames);
        return convertToRecipeResponseDtoList(recipeDocs);
    }
}
