package ru.practice.recipe_aggregator.recipe_management.recipe_service.entity;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.repository.RecipeElasticRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeEntityServiceImplTest {
    @Mock
    private RecipeElasticRepository recipeRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private RecipeEntityServiceImpl recipeService;

    private static final String INDEX_NAME = "recipe";


    @Test
    void findRecipeByName_shouldDelegateToRepository() {
        var recipe = Instancio.create(RecipeDoc.class);
        when(recipeRepository.findByName(recipe.getName())).thenReturn(Optional.of(recipe));

        Optional<RecipeDoc> result = recipeService.findByName(recipe.getName());

        assertThat(result).contains(recipe);
        verify(recipeRepository).findByName(recipe.getName());
    }

    @Test
    void findAll_shouldDelegateToRepository() {
        var recipes = List.of(
                Instancio.create(RecipeDoc.class),
                Instancio.create(RecipeDoc.class)
        );
        when(recipeRepository.findAll()).thenReturn(recipes);

        List<RecipeDoc> result = recipeService.findAll();

        assertThat(result).isEqualTo(recipes);
    }

    @Test
    void findRecipeByName_shouldReturnEmptyOptional_whenNotFound() {
        when(recipeRepository.findByName("Unknown")).thenReturn(Optional.empty());

        Optional<RecipeDoc> result = recipeService.findByName("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByIds_ShouldReturnRecipes_WhenValidIdsProvided() {
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var recipeIds = List.of(id1, id2);
        var stringIds = List.of(id1.toString(), id2.toString());
        var recipe1 = Instancio.create(RecipeDoc.class);
        var recipe2 = Instancio.create(RecipeDoc.class);
        var expectedRecipes = List.of(recipe1, recipe2);
        when(recipeRepository.findByIdsWithQuery(stringIds)).thenReturn(expectedRecipes);

        List<RecipeDoc> result = recipeService.findAllByIds(recipeIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedRecipes, result);
        verify(recipeRepository).findByIdsWithQuery(stringIds);
    }

    @Test
    void findAllByIds_ShouldReturnEmptyList_WhenEmptyIdsListProvided() {
        List<UUID> emptyIds = List.of();
        when(recipeRepository.findByIdsWithQuery(List.of())).thenReturn(List.of());

        List<RecipeDoc> result = recipeService.findAllByIds(emptyIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByIdsWithQuery(List.of());
    }

    @Test
    void findAllByIds_ShouldCallRepositoryWithCorrectStringIds() {
        var id1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        var id2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        var recipeIds = List.of(id1, id2);
        var expectedStringIds = List.of(
                "123e4567-e89b-12d3-a456-426614174000",
                "123e4567-e89b-12d3-a456-426614174001"
        );
        when(recipeRepository.findByIdsWithQuery(expectedStringIds)).thenReturn(List.of());

        recipeService.findAllByIds(recipeIds);

        verify(recipeRepository).findByIdsWithQuery(expectedStringIds);
    }

    @Test
    void saveAllWithBatches_ShouldProcessInBatches_WhenMultipleRecipes() {
        var recipes = Instancio.ofList(RecipeDoc.class).size(25).create();
        int batchSize = 10;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations, times(3)).bulkIndex(anyList(), eq(IndexCoordinates.of(INDEX_NAME)));
    }

    @Test
    void saveAllWithBatches_ShouldProcessSingleBatch_WhenRecipesLessThanBatchSize() {
        var recipes = Instancio.ofList(RecipeDoc.class).size(5).create();
        int batchSize = 10;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations, times(1)).bulkIndex(anyList(), eq(IndexCoordinates.of(INDEX_NAME)));
    }

    @Test
    void saveAllWithBatches_ShouldHandleExactBatchSize() {
        var recipes = Instancio.ofList(RecipeDoc.class).size(20).create();
        int batchSize = 10;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations, times(2)).bulkIndex(anyList(), eq(IndexCoordinates.of(INDEX_NAME)));
    }

    @Test
    void saveAllWithBatches_ShouldCreateCorrectIndexQueries() {
        var id = UUID.randomUUID();
        var recipe = Instancio.of(RecipeDoc.class)
                .set(field(RecipeDoc::getId), id)
                .create();
        var recipes = List.of(recipe);
        int batchSize = 1;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations).bulkIndex(
                argThat((List<IndexQuery> queries) ->
                        queries.size() == 1 &&
                                Objects.equals(queries.getFirst().getId(), id.toString()) &&
                                queries.getFirst().getObject() == recipe
                ),
                eq(IndexCoordinates.of(INDEX_NAME))
        );
    }

    @Test
    void saveAllWithBatches_ShouldPropagateException_WhenElasticsearchFails() {
        var recipes = Instancio.ofList(RecipeDoc.class).size(5).create();
        int batchSize = 3;

        var exception = new RuntimeException("Elasticsearch error");
        doThrow(exception).when(elasticsearchOperations).bulkIndex(anyList(), eq(IndexCoordinates.of(INDEX_NAME)));

        var thrownException = assertThrows(RuntimeException.class, () ->
                recipeService.saveAllWithBatches(recipes, batchSize)
        );
        assertEquals("Elasticsearch error", thrownException.getMessage());
    }

    @Test
    void saveAllWithBatches_ShouldProcessWithCustomBatchSize() {
        var recipes = Instancio.ofList(RecipeDoc.class).size(17).create();
        int batchSize = 7;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations, times(3)).bulkIndex(anyList(), eq(IndexCoordinates.of(INDEX_NAME)));
    }

    @Test
    void saveAllWithBatches_ShouldHandleSingleRecipe() {
        var recipe = Instancio.create(RecipeDoc.class);
        var recipes = List.of(recipe);
        int batchSize = 5;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations, times(1)).bulkIndex(
                argThat((List<IndexQuery> queries) -> queries.size() == 1),
                eq(IndexCoordinates.of(INDEX_NAME))
        );
    }

    @Test
    void saveAllWithBatches_ShouldUseCorrectIndexName() {
        var recipes = Instancio.ofList(RecipeDoc.class).size(3).create();
        int batchSize = 3;

        recipeService.saveAllWithBatches(recipes, batchSize);

        verify(elasticsearchOperations).bulkIndex(anyList(), eq(IndexCoordinates.of(INDEX_NAME)));
    }
}
