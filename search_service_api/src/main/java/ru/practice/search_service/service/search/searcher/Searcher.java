package ru.practice.search_service.service.search.searcher;

import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;

import java.util.List;

public interface Searcher {
    List<RecipeDoc> search(SearchContainer container);
}
