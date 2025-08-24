package ru.practice.search_service.service.filtering.filter;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.exception.InvalidConditionException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServingsFilterTest {
    private ServingsFilter servingsFilter;
    private List<RecipeResponseDto> recipes;
    private SearchContainer searchContainer;

    @BeforeEach
    void setUp() {
        servingsFilter = new ServingsFilter();
        recipes = new ArrayList<>();
        searchContainer = Instancio.create(SearchContainer.class);
    }

    @Test
    void filter_ShouldRemoveRecipesOutsideServingsRange() {
        // Arrange
        recipes.add(createRecipe(2));
        recipes.add(createRecipe(4));
        recipes.add(createRecipe(1));
        recipes.add(createRecipe(6));

        searchContainer.minServings(2);
        searchContainer.maxServings(5);

        // Act
        servingsFilter.filter(recipes, searchContainer);

        // Assert
        assertEquals(2, recipes.size());
        assertTrue(recipes.stream().anyMatch(r -> r.servings() == 2));
        assertTrue(recipes.stream().anyMatch(r -> r.servings() == 4));
        assertFalse(recipes.stream().anyMatch(r -> r.servings() == 1));
        assertFalse(recipes.stream().anyMatch(r -> r.servings() == 6));
    }

    @Test
    void filter_WhenMaxServingsIsNull_ShouldSetMaxServingsToMaxValue() {
        // Arrange
        searchContainer.maxServings(null);
        searchContainer.minServings(0);

        // Act & Assert
        assertDoesNotThrow(() -> servingsFilter.filter(recipes, searchContainer));
    }

    @Test
    void filter_WhenMaxServingsIsNegative_ShouldSetMaxServingsToMaxValue() {
        // Arrange
        searchContainer.maxServings(-5);
        searchContainer.minServings(0);

        // Act & Assert
        assertDoesNotThrow(() -> servingsFilter.filter(recipes, searchContainer));
    }

    @Test
    void filter_WhenMinServingsIsNull_ShouldSetMinServingsToZero() {
        // Arrange
        searchContainer.minServings(null);
        searchContainer.maxServings(10);

        // Act & Assert
        assertDoesNotThrow(() -> servingsFilter.filter(recipes, searchContainer));
    }

    @Test
    void filter_WhenBothServingsAreNull_ShouldSetDefaultValues() {
        // Arrange
        searchContainer.minServings(null);
        searchContainer.maxServings(null);

        // Act & Assert
        assertDoesNotThrow(() -> servingsFilter.filter(recipes, searchContainer));
    }

    @Test
    void filter_WhenMinServingsGreaterThanMaxServings_ShouldThrowException() {
        // Arrange
        searchContainer.minServings(10);
        searchContainer.maxServings(5);

        // Act & Assert
        var exception = assertThrows(InvalidConditionException.class,
                () -> servingsFilter.filter(recipes, searchContainer));
        assertTrue(exception.getMessage().contains("maxServings must be greater than minServings"));
    }

    @Test
    void filter_WhenMinServingsNegative_ShouldThrowException() {
        // Arrange
        searchContainer.minServings(-1);
        searchContainer.maxServings(5);

        // Act & Assert
        var exception = assertThrows(InvalidConditionException.class,
                () -> servingsFilter.filter(recipes, searchContainer));
        assertTrue(exception.getMessage().contains("minServings must be greater than -1"));
    }

    @Test
    void filter_WhenMinServingsNegativeAndMaxServingsNull_ShouldThrowExceptionAfterSettingDefaults() {
        // Arrange
        searchContainer.minServings(-1);
        searchContainer.maxServings(null);

        // Act & Assert
        var exception = assertThrows(InvalidConditionException.class,
                () -> servingsFilter.filter(recipes, searchContainer));
        assertTrue(exception.getMessage().contains("minServings must be greater than -1"));
    }

    @Test
    void filter_ShouldNotRemoveRecipesWhenServingsWithinRange() {
        // Arrange
        recipes.add(createRecipe(3));
        recipes.add(createRecipe(4));
        recipes.add(createRecipe(5));
        searchContainer.minServings(3);
        searchContainer.maxServings(5);

        // Act
        servingsFilter.filter(recipes, searchContainer);

        // Assert
        assertEquals(3, recipes.size());
    }

    @Test
    void filter_ShouldHandleEmptyRecipesList() {
        // Arrange
        searchContainer.minServings(2);
        searchContainer.maxServings(5);

        // Act
        servingsFilter.filter(recipes, searchContainer);

        // Assert
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMaxServingsIsZero_ShouldRemoveAllRecipes() {
        // Arrange
        recipes.add(createRecipe(1));
        recipes.add(createRecipe(2));
        recipes.add(createRecipe(3));
        searchContainer.minServings(0);
        searchContainer.maxServings(0);

        // Act
        servingsFilter.filter(recipes, searchContainer);

        // Assert
        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenMinServingsEqualsMaxServings_ShouldKeepOnlyExactMatches() {
        // Arrange
        recipes.add(createRecipe(3));
        recipes.add(createRecipe(4));
        recipes.add(createRecipe(3));
        searchContainer.minServings(3);
        searchContainer.maxServings(3);

        // Act
        servingsFilter.filter(recipes, searchContainer);

        // Assert
        assertEquals(2, recipes.size());
        assertTrue(recipes.stream().allMatch(r -> r.servings() == 3));
    }

    private RecipeResponseDto createRecipe(int servings) {
        RecipeResponseDto recipe = mock(RecipeResponseDto.class);
        when(recipe.servings()).thenReturn(servings);
        return recipe;
    }
}
