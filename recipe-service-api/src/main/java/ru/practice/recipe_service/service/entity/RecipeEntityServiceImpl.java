package ru.practice.recipe_service.service.entity;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_service.model.entity.RecipeEntity;
import ru.practice.recipe_service.repository.RecipeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeEntityServiceImpl implements RecipeEntityService {
    private final RecipeRepository recipeRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void save(RecipeEntity recipe) {
        try {
            recipeRepository.saveAndFlush(recipe);
        } catch (Exception e) {
            throw new EntityExistsException("Entity:" + recipe.toString() + " violates the uniqueness contract", e);
        }
    }

    @Override
    public Optional<RecipeEntity> findRecipeByName(String name) {
        return recipeRepository.findRecipeByName(name);
    }

    @Override
    public List<RecipeEntity> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public void deleteRecipeByName(String name) {
        recipeRepository.deleteRecipeEntityByName(name)
                .ifPresentOrElse(
                        recipe -> log.info("Recipe deleted: {}", recipe),
                        () -> log.info("Recipe not deleted: {}", name));
    }


    @Override
    @Transactional
    public void saveAllWithBatches(List<RecipeEntity> recipes, int batchSize) {
        for (int i = 0; i < recipes.size(); i += batchSize) {
            var batch = recipes.subList(i, Math.min(i + batchSize, recipes.size()));
            try {
                recipeRepository.saveAllAndFlush(batch);
                entityManager.clear();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
    }
}
