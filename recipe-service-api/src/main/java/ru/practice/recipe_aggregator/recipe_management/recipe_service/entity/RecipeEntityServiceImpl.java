package ru.practice.recipe_aggregator.recipe_management.recipe_service.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.repository.RecipeElasticRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeEntityServiceImpl implements RecipeEntityService {
    private static final String INDEX_NAME = "recipe";
    private final RecipeElasticRepository recipeRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Optional<RecipeDoc> findByName(String name) {
        return recipeRepository.findByName(name);
    }

    @Override
    public List<RecipeDoc> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAllWithBatches(List<RecipeDoc> recipes, int batchSize) {
        for (int i = 0; i < recipes.size(); i += batchSize) {
            var batch = recipes.subList(i, Math.min(i + batchSize, recipes.size()));
            try {
                indexBatch(batch);
                log.info("Indexed {} of {} recipes", i, batch.size());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
        log.info("All {} recipes saved", recipes.size());
    }

    private void indexBatch(List<RecipeDoc> batch) {
        var queries = batch.stream()
                .map(this::createIndexQuery)
                .collect(Collectors.toList());
        elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(INDEX_NAME));
    }

    private IndexQuery createIndexQuery(RecipeDoc recipe) {
        return new IndexQueryBuilder()
                .withId(String.valueOf(recipe.getId()))
                .withObject(recipe)
                .build();
    }
}
