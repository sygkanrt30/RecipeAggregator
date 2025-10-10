package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import jakarta.persistence.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.kafka.RecipeKafkaDto;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.entity.RecipeEntityService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {
    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeEntityService recipeEntityService;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @AfterEach
    void resetMocks() {
        Mockito.reset(recipeMapper, recipeEntityService);
    }

    @Test
    void findRecipe_shouldReturnDto_whenRecipeExists() {
        var recipeName = "Test Recipe";
        var entity = Instancio.create(RecipeDoc.class);
        var expectedDto = Instancio.create(RecipeResponseDto.class);
        when(recipeEntityService.findByName(recipeName))
                .thenReturn(Optional.of(entity));
        when(recipeMapper.toRecipeResponseDto(entity))
                .thenReturn(expectedDto);

        RecipeResponseDto result = recipeService.findRecipeByName(recipeName);

        assertThat(result).isSameAs(expectedDto);
        verify(recipeEntityService).findByName(recipeName);
        verify(recipeMapper).toRecipeResponseDto(entity);
    }

    @Test
    void findRecipe_shouldThrowException_whenRecipeNotFound() {
        var recipeName = "Non-existent Recipe";
        when(recipeEntityService.findByName(recipeName))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.findRecipeByName(recipeName))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAllByIds_ShouldReturnDtoList_WhenValidIdsProvided() {
        var recipeIds = Instancio.ofList(UUID.class).size(3).create();
        var recipeDocs = Instancio.ofList(RecipeDoc.class).size(3).create();
        var expectedDtos = Instancio.ofList(RecipeResponseDto.class).size(3).create();
        when(recipeEntityService.findAllByIds(recipeIds)).thenReturn(recipeDocs);
        for (var i = 0; i < recipeDocs.size(); i++) {
            when(recipeMapper.toRecipeResponseDto(recipeDocs.get(i))).thenReturn(expectedDtos.get(i));
        }

        var result = recipeService.findAllByIds(recipeIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedDtos, result);
        verify(recipeEntityService).findAllByIds(recipeIds);
        verify(recipeMapper, times(3)).toRecipeResponseDto(any(RecipeDoc.class));
    }

    @Test
    void findAllByIds_ShouldReturnEmptyList_WhenNoRecipesFound() {
        var recipeIds = Instancio.ofList(UUID.class).size(2).create();
        when(recipeEntityService.findAllByIds(recipeIds)).thenReturn(List.of());

        var result = recipeService.findAllByIds(recipeIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeEntityService).findAllByIds(recipeIds);
        verify(recipeMapper, never()).toRecipeResponseDto(any());
    }

    @Test
    void getIdByName_ShouldReturnRecipeId_WhenRecipeExists() {
        var id = UUID.randomUUID();
        var recipeName = Instancio.create(String.class);
        var expectedRecipe = Instancio.of(RecipeDoc.class)
                .set(field(RecipeDoc::getName), recipeName)
                .set(field(RecipeDoc::getId), id)
                .create();
        var expectedId = expectedRecipe.getId();
        when(recipeEntityService.findByName(recipeName)).thenReturn(java.util.Optional.of(expectedRecipe));

        var result = recipeService.getIdByName(recipeName);

        assertNotNull(result);
        assertEquals(expectedId, result);
        verify(recipeEntityService).findByName(recipeName);
    }

    @Test
    void getIdByName_ShouldThrowException_WhenRecipeNotFound() {
        var recipeName = Instancio.create(String.class);
        when(recipeEntityService.findByName(recipeName)).thenReturn(java.util.Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () ->
                recipeService.getIdByName(recipeName)
        );

        assertEquals("There is no recipe with that name", exception.getMessage());
        verify(recipeEntityService).findByName(recipeName);
    }

    @Test
    void saveFromKafka_shouldHandleEmptyList() {
        recipeService.saveFromKafka(Collections.emptyList());

        verify(recipeEntityService, never()).saveAllWithBatches(any(), anyInt());
    }

    @Test
    void saveFromKafka_shouldCalculateBatchSizeCorrectly() {
        when(recipeEntityService.findAll()).thenReturn(Collections.emptyList());
        var dtos = Instancio.ofList(RecipeKafkaDto.class)
                .size(23)
                .create();
        List<RecipeDoc> entities = dtos.stream()
                .map(dto -> {
                    var entity = Instancio.create(RecipeDoc.class);
                    when(recipeMapper.fromRecipeKafkaDto(dto)).thenReturn(entity);
                    return entity;
                })
                .toList();

        recipeService.saveFromKafka(dtos);

        verify(recipeEntityService, never()).saveAllWithBatches(entities, 4);
    }

    @Test
    void saveFromKafka_shouldHandleSingleItemBatch() {
        var dto = Instancio.create(RecipeKafkaDto.class);
        var entity = Instancio.create(RecipeDoc.class);
        when(recipeEntityService.findAll()).thenReturn(Collections.emptyList());
        when(recipeMapper.fromRecipeKafkaDto(dto)).thenReturn(entity);

        recipeService.saveFromKafka(List.of(dto));

        verify(recipeEntityService).findAll();
        verify(recipeMapper).fromRecipeKafkaDto(dto);
        verify(recipeEntityService).saveAllWithBatches(List.of(entity), 1);
    }

    @Test
    void saveFromKafka_shouldFilterExistingAndSaveNewRecipes() {
        var existingNames = Set.of("Existing1", "Existing2");
        var existingEntities = existingNames.stream()
                .map(name -> Instancio.of(RecipeDoc.class)
                        .set(field(RecipeDoc::getName), name)
                        .ignore(field(RecipeDoc::getId))
                        .create())
                .toList();
        var kafkaDtos = List.of(
                createKafkaDtoWithName("Existing1"),
                createKafkaDtoWithName("New1"),
                createKafkaDtoWithName("New2")
        );
        var newEntity1 = createRecipeWithName("New1");
        var newEntity2 = createRecipeWithName("New2");
        when(recipeEntityService.findAll()).thenReturn(existingEntities);
        when(recipeMapper.fromRecipeKafkaDto(argThat(dto -> dto != null && "New1".equals(dto.name()))))
                .thenReturn(newEntity1);
        when(recipeMapper.fromRecipeKafkaDto(argThat(dto -> dto != null && "New2".equals(dto.name()))))
                .thenReturn(newEntity2);

        recipeService.saveFromKafka(kafkaDtos);

        verify(recipeEntityService).findAll();
        verify(recipeMapper, never()).fromRecipeKafkaDto(argThat(dto -> dto != null && existingNames.contains(dto.name())));
        verify(recipeMapper).fromRecipeKafkaDto(argThat(dto -> dto != null && "New1".equals(dto.name())));
        verify(recipeMapper).fromRecipeKafkaDto(argThat(dto -> dto != null && "New2".equals(dto.name())));
        verify(recipeEntityService).saveAllWithBatches(
                argThat(list -> list != null && list.size() == 2 && list.containsAll(List.of(newEntity1, newEntity2))),
                eq(2)
        );
    }

    private RecipeKafkaDto createKafkaDtoWithName(String name) {
        return Instancio.of(RecipeKafkaDto.class)
                .set(field(RecipeKafkaDto::name), name)
                .create();
    }

    private RecipeDoc createRecipeWithName(String name) {
        return Instancio.of(RecipeDoc.class)
                .set(field(RecipeDoc::getName), name)
                .ignore(field(RecipeDoc::getId))
                .create();
    }
}
