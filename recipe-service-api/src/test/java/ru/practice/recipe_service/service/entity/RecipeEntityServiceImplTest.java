package ru.practice.recipe_service.service.entity;

import jakarta.persistence.EntityManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_service.model.entity.RecipeEntity;
import ru.practice.recipe_service.repository.RecipeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeEntityServiceImplTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RecipeEntityServiceImpl recipeService;

    @Test
    void findRecipeByName_shouldDelegateToRepository() {
        var recipe = Instancio.create(RecipeEntity.class);
        when(recipeRepository.findRecipeByName(recipe.getName())).thenReturn(Optional.of(recipe));

        Optional<RecipeEntity> result = recipeService.findByName(recipe.getName());

        assertThat(result).contains(recipe);
        verify(recipeRepository).findRecipeByName(recipe.getName());
    }

    @Test
    void findAll_shouldDelegateToRepository() {
        var recipes = List.of(
                Instancio.create(RecipeEntity.class),
                Instancio.create(RecipeEntity.class)
        );
        when(recipeRepository.findAll()).thenReturn(recipes);

        List<RecipeEntity> result = recipeService.findAll();

        assertThat(result).isEqualTo(recipes);
    }

    @Test
    void deleteRecipeByName_shouldLogWhenDeleted() {
        when(recipeRepository.deleteRecipeEntityByName("Existing")).thenReturn(1);
        when(recipeRepository.deleteRecipeEntityByName("Missing")).thenReturn(0);

        recipeService.deleteByName("Existing");
        recipeService.deleteByName("Missing");

        verify(recipeRepository, times(2)).deleteRecipeEntityByName(anyString());
    }

    @Test
    void saveAllWithBatches_shouldProcessInBatches() {
        List<RecipeEntity> recipes = IntStream.range(0, 105)
                .mapToObj(i -> Instancio.create(RecipeEntity.class))
                .toList();

        when(recipeRepository.saveAllAndFlush(anyList())).thenAnswer(inv -> inv.getArgument(0));

        recipeService.saveAllWithBatches(recipes, 50);

        verify(recipeRepository, times(3)).saveAllAndFlush(anyList());
        verify(entityManager, times(3)).clear();
    }

    @Test
    void saveAllWithBatches_shouldThrowExceptionOnFailure() {
        List<RecipeEntity> recipes = List.of(Instancio.create(RecipeEntity.class));
        when(recipeRepository.saveAllAndFlush(anyList())).thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> recipeService.saveAllWithBatches(recipes, 50))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB Error");
    }

    @Test
    void findRecipeByName_shouldReturnEmptyOptional_whenNotFound() {
        when(recipeRepository.findRecipeByName("Unknown")).thenReturn(Optional.empty());

        Optional<RecipeEntity> result = recipeService.findByName("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteRecipeByName_shouldNotFail_whenRecipeNotExists() {
        when(recipeRepository.deleteRecipeEntityByName("Unknown")).thenReturn(0);

        assertThatNoException()
                .isThrownBy(() -> recipeService.deleteByName("Unknown"));
    }

    @Test
    void saveAllWithBatches_shouldHandleEmptyList() {
        assertThatNoException()
                .isThrownBy(() -> recipeService.saveAllWithBatches(Collections.emptyList(), 50));

        verify(recipeRepository, never()).saveAllAndFlush(any());
    }
}
