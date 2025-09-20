package ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher;


import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;

import java.util.List;

public interface Searcher {
    List<RecipeDoc> search(SearchContainer container);
}
