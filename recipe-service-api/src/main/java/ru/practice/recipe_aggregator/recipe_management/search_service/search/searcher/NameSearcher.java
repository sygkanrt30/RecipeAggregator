package ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.repository.RecipeElasticRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NameSearcher {

    private final RecipeElasticRepository repository;

    public List<RecipeDoc> search(String name) {
        return repository.findByNameContaining(name);
    }
}
