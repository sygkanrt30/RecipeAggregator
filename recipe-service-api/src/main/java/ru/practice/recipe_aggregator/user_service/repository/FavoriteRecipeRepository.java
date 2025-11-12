package ru.practice.recipe_aggregator.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.recipe_aggregator.user_service.model.FavoriteRecipe;
import ru.practice.recipe_aggregator.user_service.model.FavoriteRecipeId;

@Repository
public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, FavoriteRecipeId> {

}
