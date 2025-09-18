package ru.practice.recipe_aggregator.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practice.recipe_aggregator.model.entity.elasticsearch.RecipeDoc;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeElasticRepository extends ElasticsearchRepository<RecipeDoc, Long> {
    List<RecipeDoc> findByNameContaining(String namePart);

    Optional<RecipeDoc> findByName(String name);

    @NonNull
    List<RecipeDoc> findAll();

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
