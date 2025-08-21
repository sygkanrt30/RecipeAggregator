package ru.practice.search_service.service.filtering;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.filter.*;

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
        for (var filter : filterChain) {
            filter.filter(recipes, searchContainer);
            log.info("list current size: {}", recipes.size());
        }
        log.info("Recipes filtration was successful");
        return recipes;
    }
}
