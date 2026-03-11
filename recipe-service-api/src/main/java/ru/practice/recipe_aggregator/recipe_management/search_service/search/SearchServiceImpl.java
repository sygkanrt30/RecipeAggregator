package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
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
        Set<RecipeDoc> recipeDocs = nameSearcher.search(name);
        return convertToRecipeResponseDtoList(recipeDocs);
    }

    private List<RecipeDto> convertToRecipeResponseDtoList(Set<RecipeDoc> recipeDocs) {
        return recipeDocs.stream()
                .map(recipeMapper::toRecipeDto)
                .peek(recipeDto -> log.trace(recipeDto.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDto> searchWithFiltering(SearchContainer container) {
        String name = container.name();
        Set<String> ingredientNames = container.ingredientNames();
        if (ingredientNamesIsNotEmpty(ingredientNames) && nameIsNotEmpty(name)) {
            return filterIntersectionOfSearchedRecipesByNameAndIngredientNames(container, name, ingredientNames);

        } else if (nameIsNotEmpty(name)) {
            Set<RecipeDoc> recipesFoundByName = nameSearcher.search(name);
            List<RecipeDto> recipeDtos = convertToRecipeResponseDtoList(recipesFoundByName);
            return filterService.processWithFilterChain(recipeDtos, container);

        } else if (ingredientNamesIsNotEmpty(ingredientNames)) {
            Set<RecipeDoc> recipesFoundByIngredientNames = ingredientsSearcher.search(ingredientNames);
            List<RecipeDto> recipeDtos = convertToRecipeResponseDtoList(recipesFoundByIngredientNames);
            return filterService.processWithFilterChain(recipeDtos, container);

        } else {
            throw new InvalidConditionException("Name and ingredientNames cannot be empty simultaneously");
        }
    }

    private boolean nameIsNotEmpty(String name) {
        return !(name == null || name.isEmpty());
    }

    private boolean ingredientNamesIsNotEmpty(Set<String> ingredientNames) {
        return !(ingredientNames == null || ingredientNames.isEmpty());
    }

    private List<RecipeDto> filterIntersectionOfSearchedRecipesByNameAndIngredientNames(SearchContainer container,
                                                                                        String name,
                                                                                        Set<String> ingredientNames) {
        Set<RecipeDoc> recipesFoundByName = nameSearcher.search(name);
        Set<RecipeDoc> recipesFoundByIngredientNames = ingredientsSearcher.search(ingredientNames);
        recipesFoundByName.retainAll(recipesFoundByIngredientNames);
        if (!recipesFoundByName.isEmpty()) {
            return filterService.processWithFilterChain(convertToRecipeResponseDtoList(recipesFoundByName), container);
        }
        log.trace("No intersection found between recipes searched by name: {} and by ingredientNames: {}", name,
                ingredientNames);
        return List.of();
    }


    @Override
    public List<RecipeDto> searchByIngredients(Set<String> ingredientNames) {
        Set<RecipeDoc> recipeDocs = ingredientsSearcher.search(ingredientNames);
        return convertToRecipeResponseDtoList(recipeDocs);
    }
}