package ru.practice.search_service.service.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practice.search_service.model.dto.mapper.DocMapper;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;
import ru.practice.search_service.model.entity.postgres.RecipeEntity;
import ru.practice.search_service.repository.postgres.RecipePostgresRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexingService {
    private final RecipePostgresRepository recipeRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final DocMapper mapper;

    @Scheduled(fixedRate = 6, timeUnit = TimeUnit.HOURS)
    public void indexAllProducts() {
        List<RecipeEntity> products = recipeRepository.findAll();
        List<RecipeDoc> documents = products.stream()
                .map(mapper::toRecipeDoc)
                .collect(Collectors.toList());
        elasticsearchOperations.save(documents);
    }
}