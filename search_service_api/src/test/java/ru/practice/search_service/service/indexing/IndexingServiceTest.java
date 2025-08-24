package ru.practice.search_service.service.indexing;

import jakarta.persistence.EntityManager;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import ru.practice.search_service.model.dto.mapper.DocMapper;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;
import ru.practice.search_service.model.entity.postgres.RecipeEntity;
import ru.practice.search_service.repository.postgres.RecipePostgresRepository;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class IndexingServiceTest {
    @Mock
    private RecipePostgresRepository recipeRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private EntityManager entityManager;

    @Mock
    private DocMapper mapper;

    @InjectMocks
    private IndexingService indexingService;

    @Test
    void indexAllProducts_ShouldProcessAllBatches() {
        // Arrange
        long totalCount = 2500L;
        when(recipeRepository.count()).thenReturn(totalCount);
        var batch1 = Instancio.ofList(RecipeEntity.class).size(1000).create();
        var batch2 = Instancio.ofList(RecipeEntity.class).size(1000).create();
        var batch3 = Instancio.ofList(RecipeEntity.class).size(500).create();
        var page1 = new PageImpl<>(batch1);
        var page2 = new PageImpl<>(batch2);
        var page3 = new PageImpl<>(batch3);
        when(recipeRepository.findAll(PageRequest.of(0, 1000, Sort.by("id"))))
                .thenReturn(page1);
        when(recipeRepository.findAll(PageRequest.of(1, 1000, Sort.by("id"))))
                .thenReturn(page2);
        when(recipeRepository.findAll(PageRequest.of(2, 1000, Sort.by("id"))))
                .thenReturn(page3);
        batch1.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));
        batch2.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));
        batch3.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(recipeRepository).count();
        verify(recipeRepository, times(3)).findAll(any(PageRequest.class));
        verify(elasticsearchOperations, times(3)).bulkIndex(anyList(), eq(IndexCoordinates.of("recipe")));
        verify(entityManager, times(3)).clear();
    }

    @Test
    void indexAllProducts_WhenEmptyDatabase_ShouldNotProcessAnyBatches() {
        // Arrange
        when(recipeRepository.count()).thenReturn(0L);

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(recipeRepository).count();
        verify(recipeRepository, never()).findAll(any(PageRequest.class));
        verify(entityManager, never()).clear();
    }

    @Test
    void indexAllProducts_WhenExactlyOneBatch_ShouldProcessOneBatch() {
        // Arrange
        long totalCount = 1000L;
        when(recipeRepository.count()).thenReturn(totalCount);
        List<RecipeEntity> batch = Instancio.ofList(RecipeEntity.class).size(1000).create();
        Page<RecipeEntity> page = new PageImpl<>(batch);
        when(recipeRepository.findAll(PageRequest.of(0, 1000, Sort.by("id"))))
                .thenReturn(page);
        batch.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(recipeRepository).count();
        verify(recipeRepository, times(1)).findAll(any(PageRequest.class));
        verify(elasticsearchOperations, times(1)).bulkIndex(anyList(), eq(IndexCoordinates.of("recipe")));
        verify(entityManager, times(1)).clear();
    }

    @Test
    void indexAllProducts_WhenLessThanBatchSize_ShouldProcessOneBatch() {
        // Arrange
        long totalCount = 500L;
        when(recipeRepository.count()).thenReturn(totalCount);
        List<RecipeEntity> batch = Instancio.ofList(RecipeEntity.class).size(500).create();
        Page<RecipeEntity> page = new PageImpl<>(batch);
        when(recipeRepository.findAll(PageRequest.of(0, 1000, Sort.by("id"))))
                .thenReturn(page);
        batch.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(recipeRepository).count();
        verify(recipeRepository, times(1)).findAll(any(PageRequest.class));
        verify(elasticsearchOperations, times(1)).bulkIndex(anyList(), eq(IndexCoordinates.of("recipe")));
        verify(entityManager, times(1)).clear();
    }

    @Test
    void indexAllProducts_ShouldCallEntityManagerClearAfterEachBatch() {
        // Arrange
        long totalCount = 2500L;
        when(recipeRepository.count()).thenReturn(totalCount);
        var batch1 = Instancio.ofList(RecipeEntity.class).size(1000).create();
        var batch2 = Instancio.ofList(RecipeEntity.class).size(1000).create();
        var batch3 = Instancio.ofList(RecipeEntity.class).size(500).create();
        var page1 = new PageImpl<>(batch1);
        var page2 = new PageImpl<>(batch2);
        var page3 = new PageImpl<>(batch3);
        when(recipeRepository.findAll(PageRequest.of(0, 1000, Sort.by("id"))))
                .thenReturn(page1);
        when(recipeRepository.findAll(PageRequest.of(1, 1000, Sort.by("id"))))
                .thenReturn(page2);
        when(recipeRepository.findAll(PageRequest.of(2, 1000, Sort.by("id"))))
                .thenReturn(page3);
        batch1.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));
        batch2.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));
        batch3.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(entityManager, times(3)).clear();
    }

    @Test
    void indexAllProducts_ShouldUseCorrectIndexCoordinates() {
        // Arrange
        long totalCount = 1000L;
        when(recipeRepository.count()).thenReturn(totalCount);
        List<RecipeEntity> batch = Instancio.ofList(RecipeEntity.class).size(1000).create();
        Page<RecipeEntity> page = new PageImpl<>(batch);
        when(recipeRepository.findAll(PageRequest.of(0, 1000, Sort.by("id"))))
                .thenReturn(page);
        batch.forEach(entity -> when(mapper.toRecipeDoc(entity)).thenReturn(mock(RecipeDoc.class)));

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(elasticsearchOperations).bulkIndex(anyList(), eq(IndexCoordinates.of("recipe")));
    }

    @Test
    void indexAllProducts_ShouldCreateIndexQueriesWithCorrectIds() {
        // Arrange
        long totalCount = 3L;
        when(recipeRepository.count()).thenReturn(totalCount);
        List<RecipeEntity> batch = Instancio.ofList(RecipeEntity.class).size(3)
                .generate(field(RecipeEntity::getId), gen -> gen.longs().range(1L, 100L))
                .create();
        Page<RecipeEntity> page = new PageImpl<>(batch);
        when(recipeRepository.findAll(PageRequest.of(0, 1000, Sort.by("id"))))
                .thenReturn(page);
        batch.forEach(entity -> {
            RecipeDoc doc = mock(RecipeDoc.class);
            when(doc.getId()).thenReturn(entity.getId());
            when(mapper.toRecipeDoc(entity)).thenReturn(doc);
        });

        // Act
        indexingService.indexAllProducts();

        // Assert
        verify(elasticsearchOperations).bulkIndex(anyList(), eq(IndexCoordinates.of("recipe")));
        batch.forEach(entity -> verify(mapper).toRecipeDoc(entity));
    }
}