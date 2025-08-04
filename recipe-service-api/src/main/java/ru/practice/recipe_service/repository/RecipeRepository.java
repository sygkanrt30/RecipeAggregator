package ru.practice.recipe_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.recipe_service.model.entity.RecipeEntity;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
    Optional<RecipeEntity> findRecipeByName(String name);

    int deleteRecipeEntityByName(String name);
}
