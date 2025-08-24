package ru.practice.search_service.service.indexing;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.search_service.model.dto.mapper.DocMapper;
import ru.practice.search_service.model.entity.postgres.RecipeEntity;
import ru.practice.search_service.repository.postgres.RecipePostgresRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class IndexingService {
    private final static int BATCH_SIZE = 1000;
    private final RecipePostgresRepository recipeRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final EntityManager entityManager;
    private final DocMapper mapper;

    @Transactional(readOnly = true)
    @Scheduled(fixedDelay = 600, initialDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void indexAllProducts() {
        long count = recipeRepository.count();
        int pages = (int) Math.ceil((double) count / BATCH_SIZE);
        for (int i = 0; i < pages; i++) {
            var batch = recipeRepository.findAll(
                    PageRequest.of(i, BATCH_SIZE, Sort.by("id"))
            ).getContent();

            indexBatch(batch);
            entityManager.clear();
            log.info("Indexed {} of {} recipes", i, batch.size());
        }
        log.info("All recipes indexed");
    }

    private void indexBatch(List<RecipeEntity> batch) {
        var queries = batch.stream()
                .map(this::createIndexQuery)
                .collect(Collectors.toList());
        elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of("recipe"));
    }

    private IndexQuery createIndexQuery(RecipeEntity product) {
        var doc = mapper.toRecipeDoc(product);
        return new IndexQueryBuilder()
                .withId(String.valueOf(doc.getId()))
                .withObject(doc)
                .build();
    }
}