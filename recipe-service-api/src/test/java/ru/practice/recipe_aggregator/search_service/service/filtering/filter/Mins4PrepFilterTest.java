package ru.practice.recipe_aggregator.search_service.service.filtering.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.search.filtering.exception.InvalidConditionException;
import ru.practice.recipe_aggregator.search.filtering.filter.Mins4PrepFilter;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Mins4PrepFilterTest {
    private Mins4PrepFilter filter;
    private SearchContainer searchContainer;
    private List<RecipeResponseDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new Mins4PrepFilter();
        searchContainer = mock(SearchContainer.class);
        recipes = new ArrayList<>();
    }

    @Test
    void filter_WhenMaxMins4PrepIsNull_ShouldSetToZeroAndReturnEarly() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(null);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer).maxMins4Prep(0);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxMins4PrepIsZero_ShouldSetToZeroAndReturnEarly() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(0);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer).maxMins4Prep(0);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxMins4PrepIsNegative_ShouldSetToZeroAndReturnEarly() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(-5);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer).maxMins4Prep(0);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxTotalMinsIsNull_ShouldNotThrowException() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(30);
        when(searchContainer.maxTotalMins()).thenReturn(null);
        RecipeResponseDto recipe1 = createRecipe(20);
        RecipeResponseDto recipe2 = createRecipe(40);
        recipes.addAll(List.of(recipe1, recipe2));

        // Act & Assert
        assertDoesNotThrow(() -> filter.filter(recipes, searchContainer));
        assertEquals(1, recipes.size());
        assertTrue(recipes.contains(recipe1));
    }

    @Test
    void filter_WhenInvalidCondition_ShouldThrowException() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(60);
        when(searchContainer.maxTotalMins()).thenReturn(30);

        // Act & Assert
        var exception = assertThrows(InvalidConditionException.class,
                () -> filter.filter(recipes, searchContainer));
        assertTrue(exception.getMessage().contains("total mins must be greater than upper limit of mins for preparing"));
    }

    @Test
    void filter_WhenValidCondition_ShouldFilterRecipesCorrectly() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(30);
        when(searchContainer.maxTotalMins()).thenReturn(60);
        RecipeResponseDto recipe1 = createRecipe(20);
        RecipeResponseDto recipe2 = createRecipe(30);
        RecipeResponseDto recipe3 = createRecipe(40);
        RecipeResponseDto recipe4 = createRecipe(10);
        recipes.addAll(List.of(recipe1, recipe2, recipe3, recipe4));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertEquals(3, recipes.size());
        assertTrue(recipes.contains(recipe1));
        assertTrue(recipes.contains(recipe2));
        assertTrue(recipes.contains(recipe4));
        assertFalse(recipes.contains(recipe3));
    }

    @Test
    void filter_WhenAllRecipesExceedLimit_ShouldRemoveAll() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(15);
        when(searchContainer.maxTotalMins()).thenReturn(60);
        RecipeResponseDto recipe1 = createRecipe(20);
        RecipeResponseDto recipe2 = createRecipe(25);
        RecipeResponseDto recipe3 = createRecipe(30);
        recipes.addAll(List.of(recipe1, recipe2, recipe3));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenNoRecipesExceedLimit_ShouldKeepAll() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(60);
        when(searchContainer.maxTotalMins()).thenReturn(90);
        RecipeResponseDto recipe1 = createRecipe(20);
        RecipeResponseDto recipe2 = createRecipe(30);
        RecipeResponseDto recipe3 = createRecipe(45);
        recipes.addAll(List.of(recipe1, recipe2, recipe3));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertEquals(3, recipes.size());
        assertTrue(recipes.containsAll(List.of(recipe1, recipe2, recipe3)));
    }

    @Test
    void filter_WithEmptyList_ShouldNotThrowException() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(30);
        when(searchContainer.maxTotalMins()).thenReturn(60);

        // Act & Assert
        assertDoesNotThrow(() -> filter.filter(recipes, searchContainer));
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxTotalMinsIsNullAndValidPrepTime_ShouldFilterNormally() {
        // Arrange
        when(searchContainer.maxMins4Prep()).thenReturn(30);
        when(searchContainer.maxTotalMins()).thenReturn(null);
        RecipeResponseDto recipe1 = createRecipe(25);
        RecipeResponseDto recipe2 = createRecipe(35);
        recipes.addAll(List.of(recipe1, recipe2));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertEquals(1, recipes.size());
        assertTrue(recipes.contains(recipe1));
    }

    private RecipeResponseDto createRecipe(int mins4Prep) {
        RecipeResponseDto recipe = mock(RecipeResponseDto.class);
        when(recipe.mins4Prep()).thenReturn(mins4Prep);
        return recipe;
    }
}
