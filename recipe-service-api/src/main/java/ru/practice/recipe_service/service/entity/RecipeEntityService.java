package ru.practice.recipe_service.service.entity;

import ru.practice.recipe_service.model.entity.RecipeEntity;

import java.util.List;
import java.util.Optional;

public interface RecipeEntityService {
    Optional<RecipeEntity> findByName(String name);

    List<RecipeEntity> findAll();

    void deleteByName(String name);

    void saveAllWithBatches(List<RecipeEntity> recipes, int batchSize);

    List<RecipeEntity> findByIds(List<Long> ids);
}
