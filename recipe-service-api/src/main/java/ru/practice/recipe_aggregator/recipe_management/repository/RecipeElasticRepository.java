package ru.practice.recipe_aggregator.recipe_management.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeElasticRepository extends ElasticsearchRepository<RecipeDoc, Long> {
    List<RecipeDoc> findByNameContaining(String namePart);

    Optional<RecipeDoc> findByName(String name);

    @NonNull
    List<RecipeDoc> findAll();

    @NonNull
    List<RecipeDoc> findAllById(@NonNull List<UUID> ids);

    @Query("""
            {
              "nested": {
                "path": "ingredients",
                "query": {
                  "terms": {
                    "ingredients.name": ?0,
                    "boost": 2.0
                  }
                }
              }
            }
            """)
    List<RecipeDoc> findByIngredientsContainingAny(List<String> ingredients);
}
