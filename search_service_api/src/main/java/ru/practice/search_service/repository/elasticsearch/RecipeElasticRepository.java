package ru.practice.search_service.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;

import java.util.List;

@Repository
public interface RecipeElasticRepository extends ElasticsearchRepository<RecipeDoc, Long> {
    List<RecipeDoc> findByNameContaining(String namePart);

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
