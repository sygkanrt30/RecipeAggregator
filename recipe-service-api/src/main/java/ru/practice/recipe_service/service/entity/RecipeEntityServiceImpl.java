package ru.practice.recipe_service.service.entity;

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
    public Optional<RecipeEntity> findRecipeByName(String name) {
        return recipeRepository.findRecipeByName(name);
    }

    @Override
    public List<RecipeEntity> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteRecipeByName(String name) {
        int isDeleted = recipeRepository.deleteRecipeEntityByName(name);
        if (isDeleted > 0) {
            log.info("Recipe with name: {} deleted", name);
        } else {
            log.info("Recipe not deleted: {}", name);
        }
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

    @Override
    public Optional<RecipeEntity> findRecipeById(long id) {
        return recipeRepository.findById(id);
    }
}
