package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.repository.RecipeElasticRepository;
import ru.practice.shared.dto.RecipeDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipeService {

    private static final String INDEX_NAME = "recipe";
    private static final int BATCHES_SIZE = 10;
    private final RecipeElasticRepository recipeRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RecipeMapper mapper;

    @Override
    public List<RecipeDto> findAllByIds(List<UUID> recipeIds, int page, int size) {
        log.debug("search all recipes which id in {} ", recipeIds.toString());
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findByIdIn(recipeIds, pageable).stream()
                .map(mapper::toRecipeDto)
                .toList();
    }

    @Override
    public Set<UUID> findExistingIds(Set<UUID> recipeIds) {
        if (recipeIds.isEmpty()) {
            return Collections.emptySet();
        }
        Pageable pageable = PageRequest.of(0, recipeIds.size());
        return recipeRepository.findByIdIn(recipeIds, pageable)
                .stream()
                .map(RecipeDoc::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public UUID getIdByName(String recipeName) {
        return findByName(recipeName)
                .orElseThrow(() -> new EntityNotFoundException("There is no recipe with that name"))
                .getId();
    }

    private Optional<RecipeDoc> findByName(String name) {
        log.debug("search recipes with name : {}", name);
        return recipeRepository.findByName(name);
    }

    @Override
    @Transactional
    public void saveAllWithBatches(List<RecipeDoc> recipes) {
        int batchSize = Math.min(recipes.size(), BATCHES_SIZE);
        log.debug("start saving {} recipes; batch size:{}", recipes.size(), batchSize);
        for (int i = 0; i < recipes.size(); i += batchSize) {
            var batch = recipes.subList(i, Math.min(i + batchSize, recipes.size()));
            try {
                indexBatch(batch);
                log.trace("Indexed {} of {} recipes", i, batch.size());
            } catch (Exception e) {
                throw new SaveRecipeException(e.getMessage(), e.getCause());
            }
        }
        log.info("All {} recipes successfully saved", recipes.size());
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
