package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.factory.FilterChainFactory;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.Filter;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@Service
@Slf4j
public class FilterServiceImpl implements FilterService {

    private final List<Filter> filterChain;

    public FilterServiceImpl() {
        filterChain = FilterChainFactory.createDefaultFilterChain();
    }

    @Override
    public List<RecipeDto> processWithFilterChain(List<RecipeDto> recipes, SearchContainer searchContainer) {
        int size = recipes.size();
        log.debug("list start size: {}", size);
        for (var filter : filterChain) {
            filter.filter(recipes, searchContainer);

            int currentSize = recipes.size();
            log.trace("list current size: {}", currentSize);

            size -= currentSize;
            log.debug("filter {} removed {} recipes from list", filter.getFilterName(), size);
        }
        log.info("Recipes filtration was successful");
        return recipes;
    }
}
