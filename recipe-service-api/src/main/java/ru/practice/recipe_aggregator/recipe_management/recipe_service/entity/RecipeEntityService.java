package ru.practice.recipe_aggregator.recipe_management.recipe_service.entity;

import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;

import java.util.List;
import java.util.Optional;

public interface RecipeEntityService {
    Optional<RecipeDoc> findByName(String name);

    List<RecipeDoc> findAll();

    void saveAllWithBatches(List<RecipeDoc> recipes, int batchSize);
}
