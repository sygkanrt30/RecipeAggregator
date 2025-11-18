package ru.practice.recipe_aggregator.recipe_management.processor;

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
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.shared.dto.RecipeDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeConsumerProcessorTest {
    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeConsumerProcessor recipeConsumerProcessor;

    @AfterEach
    void resetMocks() {
        Mockito.reset(recipeMapper, recipeService);
    }

    @Test
    void saveFromKafka_shouldHandleEmptyList() {
        recipeConsumerProcessor.saveFromKafka(Collections.emptyList());

        verify(recipeService, never()).saveAllWithBatches(any());
    }

    @Test
    void saveFromKafka_shouldHandleSingleItemBatch() {
        var dto = Instancio.create(RecipeDto.class);
        var entity = Instancio.create(RecipeDoc.class);
        when(recipeMapper.fromRecipeDto(dto)).thenReturn(entity);

        recipeConsumerProcessor.saveFromKafka(List.of(dto));

        verify(recipeService).saveAllWithBatches(List.of(entity));
    }

    @Test
    void saveFromKafka_shouldFilterExistingAndSaveNewRecipes() {
        // given
        var existingIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        var newIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        var allIds = Stream.concat(existingIds.stream(), newIds.stream())
                .collect(Collectors.toSet());
        var existingDtos = existingIds.stream()
                .map(id -> Instancio.of(RecipeDto.class).set(field(RecipeDto::id), id).create())
                .toList();
        var newDtos = newIds.stream()
                .map(id -> Instancio.of(RecipeDto.class).set(field(RecipeDto::id), id).create())
                .toList();
        var kafkaDtos = Stream.concat(existingDtos.stream(), newDtos.stream()).toList();
        var newEntities = newIds.stream()
                .map(id -> Instancio.of(RecipeDoc.class).set(field(RecipeDoc::getId), id).create())
                .toList();
        when(recipeService.findExistingIds(allIds)).thenReturn(existingIds);
        for (int i = 0; i < newDtos.size(); i++) {
            when(recipeMapper.fromRecipeDto(newDtos.get(i))).thenReturn(newEntities.get(i));
        }

        // when
        recipeConsumerProcessor.saveFromKafka(kafkaDtos);

        // then
        verify(recipeService).saveAllWithBatches(newEntities);
        existingDtos.forEach(dto ->
                verify(recipeMapper, never()).fromRecipeDto(dto));
    }
}
