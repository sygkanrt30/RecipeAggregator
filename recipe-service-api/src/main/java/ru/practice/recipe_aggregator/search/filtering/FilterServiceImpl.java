package ru.practice.recipe_aggregator.search.filtering;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.search.filtering.filter.*;

import java.util.List;

@Service
@Slf4j
public class FilterServiceImpl implements FilterService {
    private final List<Filter> filterChain;

    public FilterServiceImpl() {
        filterChain = List.of(
                new Mins4CookFilter(),
                new Mins4PrepFilter(),
                new TotalMinsFilter(),
                new ServingsFilter()
        );
    }

    @Override
    public List<RecipeResponseDto> processWithFilterChain(List<RecipeResponseDto> recipes, SearchContainer searchContainer) {
        log.info("list start size: {}", recipes.size());
        for (var filter : filterChain) {
            filter.filter(recipes, searchContainer);
            log.info("list current size: {}", recipes.size());
        }
        log.info("Recipes filtration was successful");
        return recipes;
    }
}
