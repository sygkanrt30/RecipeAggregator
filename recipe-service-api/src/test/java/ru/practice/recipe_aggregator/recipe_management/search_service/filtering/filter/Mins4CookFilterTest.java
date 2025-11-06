package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.Mins4CookFilter;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Mins4CookFilterTest {

    private Mins4CookFilter filter;
    private SearchContainer searchContainer;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new Mins4CookFilter();
        searchContainer = mock(SearchContainer.class);
        recipes = new ArrayList<>();
    }

    @Test
    void filter_WhenMaxMins4CookIsNull_ShouldSetToZeroAndReturnEarly() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(null);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer).maxMinsForCooking(0);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxMins4CookIsZero_ShouldSetToZeroAndReturnEarly() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(0);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer).maxMinsForCooking(0);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxMins4CookIsNegative_ShouldSetToZeroAndReturnEarly() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(-5);

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        verify(searchContainer).maxMinsForCooking(0);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenInvalidCondition_ShouldThrowException() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(60);
        when(searchContainer.maxTotalMinutes()).thenReturn(30);

        // Act & Assert
        var exception = assertThrows(InvalidConditionException.class,
                () -> filter.filter(recipes, searchContainer));

        assertTrue(exception.getMessage().contains("total mins must be greater than upper limit of mins for cooking"));
    }

    @Test
    void filter_WhenValidCondition_ShouldFilterRecipesCorrectly() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(30);
        when(searchContainer.maxTotalMinutes()).thenReturn(60);
        RecipeDto recipe1 = createRecipe(20);
        RecipeDto recipe2 = createRecipe(30);
        RecipeDto recipe3 = createRecipe(40);
        RecipeDto recipe4 = createRecipe(10);
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
        when(searchContainer.maxMinsForCooking()).thenReturn(15);
        when(searchContainer.maxTotalMinutes()).thenReturn(60);
        RecipeDto recipe1 = createRecipe(20);
        RecipeDto recipe2 = createRecipe(25);
        RecipeDto recipe3 = createRecipe(30);

        recipes.addAll(List.of(recipe1, recipe2, recipe3));

        // Act
        filter.filter(recipes, searchContainer);

        // Assert
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenNoRecipesExceedLimit_ShouldKeepAll() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(60);
        when(searchContainer.maxTotalMinutes()).thenReturn(90);
        RecipeDto recipe1 = createRecipe(20);
        RecipeDto recipe2 = createRecipe(30);
        RecipeDto recipe3 = createRecipe(45);
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
        when(searchContainer.maxMinsForCooking()).thenReturn(30);
        when(searchContainer.maxTotalMinutes()).thenReturn(60);

        // Act & Assert
        assertDoesNotThrow(() -> filter.filter(recipes, searchContainer));
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxTotalMinsIsNull_ShouldNotThrowException() {
        // Arrange
        when(searchContainer.maxMinsForCooking()).thenReturn(30);
        when(searchContainer.maxTotalMinutes()).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> filter.filter(recipes, searchContainer));
    }

    private RecipeDto createRecipe(int mins4Cook) {
        RecipeDto recipe = mock(RecipeDto.class);
        when(recipe.minsForCooking()).thenReturn(Duration.ofMinutes(mins4Cook));
        return recipe;
    }
}
