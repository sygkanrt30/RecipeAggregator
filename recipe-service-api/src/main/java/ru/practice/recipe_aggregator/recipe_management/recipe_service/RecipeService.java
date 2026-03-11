package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RecipeService {

    Set<String> findExistingNames(Set<String> recipeNames);

    UUID getIdByName(String recipeName);

    void saveAllWithBatches(List<RecipeDoc> recipes);

    List<RecipeDto> findAllByIds(List<UUID> recipeIds, int page, int size);
}
