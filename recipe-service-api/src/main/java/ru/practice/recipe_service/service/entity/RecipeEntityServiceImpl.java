package ru.practice.recipe_service.service.entity;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_service.model.entity.RecipeEntity;
import ru.practice.recipe_service.repository.RecipeRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeEntityServiceImpl implements RecipeEntityService {
    private final RecipeRepository recipeRepository;

    @Override
    @Transactional
    public void trySaveRecipe(RecipeEntity recipe) {
        try {
            recipeRepository.save(recipe);
        } catch (Exception e) {
            throw new EntityExistsException("Entity:" + recipe.toString() + " violates the uniqueness contract", e);
        }
    }

    @Override
    public Optional<RecipeEntity> findRecipeByName(String name) {
        return recipeRepository.findRecipeByName(name);
    }

    @Override
    @Transactional
    public void tryToSaveAllRecipes(Collection<RecipeEntity> recipes) {
        try {
            recipeRepository.saveAll(recipes);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
}
