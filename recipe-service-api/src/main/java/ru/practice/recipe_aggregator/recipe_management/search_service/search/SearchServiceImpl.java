package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.Searcher;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final FilterService filterService;
    private final RecipeMapper recipeMapper;
    @Qualifier("nameSearcher")
    private final Searcher nameSearcher;
    @Qualifier("ingredientsSearcher")
    private final Searcher ingredientsSearcher;


    @Override
    public List<RecipeResponseDto> searchByName(SearchContainer container) {
        throwIfNameEmptyOrNull(container);
        List<RecipeDoc> recipeDocs = nameSearcher.search(container);
        return convertToRecipeResponseDtoList(recipeDocs);
    }

    private void throwIfNameEmptyOrNull(SearchContainer container) {
        if (container.name() == null || container.name().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be empty or null");
        }
    }

    private List<RecipeResponseDto> convertToRecipeResponseDtoList(List<RecipeDoc> recipeDocs) {
        return recipeDocs.stream()
                .map(recipeMapper::toRecipeResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeResponseDto> searchByNameWithFiltering(SearchContainer container) {
        List<RecipeResponseDto> recipes = searchByName(container);
        if (recipes.isEmpty()) {
            log.info("No recipes found for name {}", container.name());
            return recipes;
        }
        filterService.processWithFilterChain(recipes, container);
        return recipes;
    }

    @Override
    public List<RecipeResponseDto> searchByIngredientsWithFiltering(SearchContainer container) {
        List<RecipeResponseDto> recipes = searchByIngredients(container);
        if (recipes.isEmpty()) {
            log.info("No recipes found for ingredients {}", container.ingredientsName());
            return recipes;
        }
        filterService.processWithFilterChain(recipes, container);
        return recipes;
    }

    @Override
    public List<RecipeResponseDto> searchByIngredients(SearchContainer container) {
        throwIfIngredientsEmptyOrNull(container);
        List<RecipeDoc> recipeDocs = ingredientsSearcher.search(container);
        return convertToRecipeResponseDtoList(recipeDocs);
    }

    private void throwIfIngredientsEmptyOrNull(SearchContainer container) {
        if (container.ingredientsName() == null || container.ingredientsName().isEmpty()) {
            throw new IllegalArgumentException("Ingredients name cannot be empty or null");
        }
    }
}
