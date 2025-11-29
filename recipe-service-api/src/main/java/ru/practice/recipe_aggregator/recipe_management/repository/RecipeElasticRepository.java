package ru.practice.recipe_aggregator.recipe_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;

import java.util.*;

@Repository
public interface RecipeElasticRepository extends ElasticsearchRepository<RecipeDoc, UUID> {

    @Query("""
            {
                "bool": {
                    "should": [
                        {
                            "match_phrase_prefix": {
                                "name": {
                                    "query": "?0",
                                    "slop": 10,
                                    "max_expansions": 50
                                }
                            }
                        },
                        {
                            "wildcard": {
                                "name": {
                                    "value": "*?0*",
                                    "case_insensitive": true
                                }
                            }
                        }
                    ]
                }
            }
            """)
    Set<RecipeDoc> findByNameMultiMatch(String name);

    Optional<RecipeDoc> findByName(String name);

    Page<RecipeDoc> findByIdIn(Collection<UUID> id, Pageable pageable);

    List<RecipeDoc> findByNameIn(Collection<String> names);

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
    Set<RecipeDoc> findByIngredientsContainingAny(Collection<String> ingredients);
}
