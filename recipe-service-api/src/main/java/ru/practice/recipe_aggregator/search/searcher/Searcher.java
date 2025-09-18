package ru.practice.recipe_aggregator.search.searcher;


import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.entity.elasticsearch.RecipeDoc;

import java.util.List;

public interface Searcher {
    List<RecipeDoc> search(SearchContainer container);
}
