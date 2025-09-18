package ru.practice.recipe_aggregator.search.searcher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.repository.RecipeElasticRepository;

import java.util.List;

@Component
@Qualifier("ingredientsSearcher")
@RequiredArgsConstructor
public class IngredientsSearcher implements Searcher {
    private final RecipeElasticRepository repository;

    @Override
    public List<RecipeDoc> search(SearchContainer container) {
        return repository.findByIngredientsContainingAny(container.ingredientsName());
    }
}
