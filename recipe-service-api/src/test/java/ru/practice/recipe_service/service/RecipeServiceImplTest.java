package ru.practice.recipe_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_service.model.dto.kafka.RecipeKafkaDto;
import ru.practice.recipe_service.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.model.entity.RecipeEntity;
import ru.practice.recipe_service.service.entity.RecipeEntityService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
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
        var entity = Instancio.create(RecipeEntity.class);
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
    void deleteRecipe_shouldDelegateToEntityService() {
        var recipeName = "Recipe to delete";

        recipeService.deleteRecipeByName(recipeName);

        verify(recipeEntityService).deleteByName(recipeName.trim());
    }

    @Test
    void deleteRecipe_shouldTrimInput() {
        var recipeName = "  Recipe with spaces  ";

        recipeService.deleteRecipeByName(recipeName);

        verify(recipeEntityService).deleteByName("Recipe with spaces");
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
        List<RecipeEntity> entities = dtos.stream()
                .map(dto -> {
                    RecipeEntity entity = Instancio.create(RecipeEntity.class);
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
        var entity = Instancio.create(RecipeEntity.class);

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
                .map(name -> Instancio.of(RecipeEntity.class)
                        .set(field(RecipeEntity::getName), name)
                        .ignore(field(RecipeEntity::getId))
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

    private RecipeEntity createRecipeWithName(String name) {
        return Instancio.of(RecipeEntity.class)
                .set(field(RecipeEntity::getName), name)
                .ignore(field(RecipeEntity::getId))
                .create();
    }
}
