package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.factory;

import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.*;

import java.util.List;

public interface FilterChainFactory {
    static List<Filter> createDefaultFilterChain() {
        return List.of(
                new CookingTimeFilter(),
                new PreparingTimeFilter(),
                new TotalTimeFilter(),
                new ServingsFilter()
        );
    }
}
