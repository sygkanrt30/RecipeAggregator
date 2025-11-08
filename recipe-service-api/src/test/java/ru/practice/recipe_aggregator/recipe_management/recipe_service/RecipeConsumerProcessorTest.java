package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.processor.RecipeConsumerProcessor;
import ru.practice.shared.dto.RecipeDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeConsumerProcessorTest {
    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeService recipeEntityService;

    @InjectMocks
    private RecipeConsumerProcessor recipeService;

    @AfterEach
    void resetMocks() {
        Mockito.reset(recipeMapper, recipeEntityService);
    }

    @Test
    void saveFromKafka_shouldHandleEmptyList() {
        recipeService.saveFromKafka(Collections.emptyList());

        verify(recipeEntityService, never()).saveAllWithBatches(any());
    }

    @Test
    void saveFromKafka_shouldHandleSingleItemBatch() {
        var dto = Instancio.create(RecipeDto.class);
        var entity = Instancio.create(RecipeDoc.class);
        when(recipeEntityService.findAll()).thenReturn(Collections.emptyList());
        when(recipeMapper.fromRecipeKafkaDto(dto)).thenReturn(entity);

        recipeService.saveFromKafka(List.of(dto));

        verify(recipeEntityService).findAll();
        verify(recipeMapper).fromRecipeKafkaDto(dto);
        verify(recipeEntityService).saveAllWithBatches(List.of(entity));
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
                argThat(list -> list != null && list.size() == 2 && list.containsAll(List.of(newEntity1, newEntity2)))
        );
    }

    private RecipeDto createKafkaDtoWithName(String name) {
        return Instancio.of(RecipeDto.class)
                .set(field(RecipeDto::name), name)
                .create();
    }

    private RecipeDoc createRecipeWithName(String name) {
        return Instancio.of(RecipeDoc.class)
                .set(field(RecipeDoc::getName), name)
                .ignore(field(RecipeDoc::getId))
                .create();
    }
}
