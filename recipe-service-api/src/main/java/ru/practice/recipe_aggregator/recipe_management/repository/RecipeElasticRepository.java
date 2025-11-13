package ru.practice.recipe_aggregator.recipe_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeElasticRepository extends ElasticsearchRepository<RecipeDoc, UUID> {

    List<RecipeDoc> findByNameContaining(String namePart);

    Optional<RecipeDoc> findByName(String name);

    Page<RecipeDoc> findByIdIn(Collection<UUID> id, Pageable pageable);

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
    List<RecipeDoc> findByIngredientsContainingAny(Collection<String> ingredients);
}
