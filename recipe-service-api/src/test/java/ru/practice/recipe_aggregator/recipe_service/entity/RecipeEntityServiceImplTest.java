package ru.practice.recipe_aggregator.recipe_service.entity;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.entity.RecipeEntityServiceImpl;
import ru.practice.recipe_aggregator.recipe_management.repository.RecipeElasticRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeEntityServiceImplTest {
    @Mock
    private RecipeElasticRepository recipeRepository;

    @InjectMocks
    private RecipeEntityServiceImpl recipeService;

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

    //todo написать тесты на saveAllWithBatches
}
