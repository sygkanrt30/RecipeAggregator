package ru.practice.recipe_service.service.entity;

import ru.practice.recipe_service.model.entity.RecipeEntity;

import java.util.Collection;
import java.util.Optional;

public interface RecipeEntityService {
    void trySaveRecipe(RecipeEntity recipe);

    Optional<RecipeEntity> findRecipeByName(String name);

    void tryToSaveAllRecipes(Collection<RecipeEntity> recipes);
}
