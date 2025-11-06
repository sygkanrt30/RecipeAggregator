package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.TotalMinsFilter;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TotalMinsFilterTest {
    private TotalMinsFilter filter;
    private SearchContainer searchContainer;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new TotalMinsFilter();
        searchContainer = mock(SearchContainer.class);
        recipes = new ArrayList<>();
    }

    @Test
    void filter_WhenMaxTotalMinsIsNull_ShouldReturnEarly() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(null);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer, never()).maxMinsForPreparing();
        verify(searchContainer, never()).maxMinsForCooking();
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxTotalMinsIsZero_ShouldReturnEarly() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(0);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer, never()).maxMinsForPreparing();
        verify(searchContainer, never()).maxMinsForCooking();
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxTotalMinsIsNegative_ShouldReturnEarly() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(-5);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer, never()).maxMinsForPreparing();
        verify(searchContainer, never()).maxMinsForCooking();
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxMins4CookIsNull_ShouldNotValidateCondition() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(60);
        when(searchContainer.maxMinsForPreparing()).thenReturn(20);
        when(searchContainer.maxMinsForCooking()).thenReturn(null);
        RecipeDto recipe1 = createRecipe(50);
        RecipeDto recipe2 = createRecipe(70);
        recipes.addAll(List.of(recipe1, recipe2));

        // Act & Assert
        assertDoesNotThrow(() -> filter.filter(recipes, searchContainer));
        assertEquals(1, recipes.size());
        assertTrue(recipes.contains(recipe1));
    }

    @Test
    void filter_WhenInvalidCondition_ShouldThrowException() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(60);
        when(searchContainer.maxMinsForPreparing()).thenReturn(40);
        when(searchContainer.maxMinsForCooking()).thenReturn(30);

        // Act & Assert
        var exception = assertThrows(InvalidConditionException.class,
                () -> filter.filter(recipes, searchContainer));

        assertTrue(exception.getMessage().contains("total mins must be greater than sum of max mins for cooking and for preparing"));
    }

    @Test
    void filter_WhenValidCondition_ShouldFilterRecipesCorrectly() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(60);
        when(searchContainer.maxMinsForPreparing()).thenReturn(20);
        when(searchContainer.maxMinsForCooking()).thenReturn(30);
        RecipeDto recipe1 = createRecipe(50);
        RecipeDto recipe2 = createRecipe(60);
        RecipeDto recipe3 = createRecipe(70);
        RecipeDto recipe4 = createRecipe(40);
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
        when(searchContainer.maxTotalMinutes()).thenReturn(50);
        when(searchContainer.maxMinsForPreparing()).thenReturn(20);
        when(searchContainer.maxMinsForCooking()).thenReturn(20);
        RecipeDto recipe1 = createRecipe(60);
        RecipeDto recipe2 = createRecipe(70);
        RecipeDto recipe3 = createRecipe(80);
        recipes.addAll(List.of(recipe1, recipe2, recipe3));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenNoRecipesExceedLimit_ShouldKeepAll() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(80);
        when(searchContainer.maxMinsForPreparing()).thenReturn(30);
        when(searchContainer.maxMinsForCooking()).thenReturn(30);
        RecipeDto recipe1 = createRecipe(50);
        RecipeDto recipe2 = createRecipe(60);
        RecipeDto recipe3 = createRecipe(70);

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
        when(searchContainer.maxTotalMinutes()).thenReturn(60);
        when(searchContainer.maxMinsForPreparing()).thenReturn(20);
        when(searchContainer.maxMinsForCooking()).thenReturn(30);

        // Act & Assert
        assertDoesNotThrow(() -> filter.filter(recipes, searchContainer));
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenValidConditionWithEqualSum_ShouldWorkCorrectly() {
        // Arrange
        when(searchContainer.maxTotalMinutes()).thenReturn(50);
        when(searchContainer.maxMinsForPreparing()).thenReturn(20);
        when(searchContainer.maxMinsForCooking()).thenReturn(30);
        RecipeDto recipe1 = createRecipe(50);
        RecipeDto recipe2 = createRecipe(60);

        recipes.addAll(List.of(recipe1, recipe2));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertEquals(1, recipes.size());
        assertTrue(recipes.contains(recipe1));
    }

    private RecipeDto createRecipe(int totalMins) {
        RecipeDto recipe = mock(RecipeDto.class);
        when(recipe.totalMins()).thenReturn(Duration.ofMinutes(totalMins));
        return recipe;
    }
}
