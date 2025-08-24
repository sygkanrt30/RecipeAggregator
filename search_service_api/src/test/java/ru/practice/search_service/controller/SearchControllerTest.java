package ru.practice.search_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.service.filtering.FilterService;
import ru.practice.search_service.service.search.SearchService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @Mock
    private FilterService filterService;

    @InjectMocks
    private SearchController searchController;

    @Test
    void searchByName_ShouldReturnResults() {
        // Arrange
        var name = "pasta";
        var expectedResults = List.of(mock(RecipeResponseDto.class), mock(RecipeResponseDto.class));
        when(searchService.searchByName(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByName(name);

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByName(argThat(container ->
                container.name().equals(name) &&
                        container.ingredientsName() == null
        ));
    }

    @Test
    void searchByIngredients_ShouldReturnResults() {
        // Arrange
        var ingredients = List.of("chicken", "rice");
        var expectedResults = List.of(mock(RecipeResponseDto.class), mock(RecipeResponseDto.class));
        when(searchService.searchByIngredients(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByIngredients(ingredients);

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByIngredients(argThat(container ->
                container.ingredientsName().equals(ingredients) &&
                        container.name() == null
        ));
    }

    @Test
    void filter_ShouldReturnFilteredResults() {
        // Arrange
        var recipes = List.of(mock(RecipeResponseDto.class), mock(RecipeResponseDto.class));
        var filteredRecipes = List.of(mock(RecipeResponseDto.class));
        var maxMins4Cook = 30;
        var maxTotalMins = 60;
        var maxMins4Prep = 15;
        var minServings = 2;
        var maxServings = 4;

        when(filterService.processWithFilterChain(anyList(), any(SearchContainer.class))).thenReturn(filteredRecipes);

        // Act
        var result = searchController.filter(recipes, maxMins4Cook, maxTotalMins, maxMins4Prep, minServings, maxServings);

        // Assert
        assertEquals(filteredRecipes, result);
        verify(filterService).processWithFilterChain(eq(recipes), argThat(container ->
                container.maxMins4Cook() == maxMins4Cook &&
                        container.maxTotalMins() == maxTotalMins &&
                        container.maxMins4Prep() == maxMins4Prep &&
                        container.minServings() == minServings &&
                        container.maxServings() == maxServings
        ));
    }

    @Test
    void filter_WithNullParameters_ShouldHandleNulls() {
        // Arrange
        var recipes = List.of(mock(RecipeResponseDto.class));
        var filteredRecipes = List.of(mock(RecipeResponseDto.class));
        when(filterService.processWithFilterChain(anyList(), any(SearchContainer.class))).thenReturn(filteredRecipes);

        // Act
        var result = searchController.filter(recipes, null, null, null, null, null);

        // Assert
        assertEquals(filteredRecipes, result);
        verify(filterService).processWithFilterChain(eq(recipes), argThat(container ->
                container.maxMins4Cook() == null &&
                        container.maxTotalMins() == null &&
                        container.maxMins4Prep() == null &&
                        container.minServings() == null &&
                        container.maxServings() == null
        ));
    }

    @Test
    void searchByIngredientsWithFiltering_ShouldReturnFilteredResults() {
        // Arrange
        var ingredients = List.of("chicken", "rice");
        var maxMins4Cook = 30;
        var maxTotalMins = 60;
        var maxMins4Prep = 15;
        var minServings = 2;
        var maxServings = 4;
        var expectedResults = List.of(mock(RecipeResponseDto.class));
        when(searchService.searchByIngredientsWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByIngredientsWithFiltering(
                ingredients, maxMins4Cook, maxTotalMins, maxMins4Prep, minServings, maxServings
        );

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByIngredientsWithFiltering(argThat(container ->
                container.ingredientsName().equals(ingredients) &&
                        container.maxMins4Cook() == maxMins4Cook &&
                        container.maxTotalMins() == maxTotalMins &&
                        container.maxMins4Prep() == maxMins4Prep &&
                        container.minServings() == minServings &&
                        container.maxServings() == maxServings
        ));
    }

    @Test
    void searchByIngredientsWithFiltering_WithNullFilterParameters_ShouldHandleNulls() {
        // Arrange
        var ingredients = List.of("chicken", "rice");
        var expectedResults = List.of(mock(RecipeResponseDto.class));
        when(searchService.searchByIngredientsWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByIngredientsWithFiltering(
                ingredients, null, null, null, null, null
        );

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByIngredientsWithFiltering(argThat(container ->
                container.ingredientsName().equals(ingredients) &&
                        container.maxMins4Cook() == null &&
                        container.maxTotalMins() == null &&
                        container.maxMins4Prep() == null &&
                        container.minServings() == null &&
                        container.maxServings() == null
        ));
    }

    @Test
    void searchByNameWithFiltering_ShouldReturnFilteredResults() {
        // Arrange
        var name = "pasta";
        var maxMins4Cook = 30;
        var maxTotalMins = 60;
        var maxMins4Prep = 15;
        var minServings = 2;
        var maxServings = 4;
        var expectedResults = List.of(mock(RecipeResponseDto.class));
        when(searchService.searchByNameWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByNameWithFiltering(
                name, maxMins4Cook, maxTotalMins, maxMins4Prep, minServings, maxServings
        );

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByNameWithFiltering(argThat(container ->
                container.name().equals(name) &&
                        container.maxMins4Cook() == maxMins4Cook &&
                        container.maxTotalMins() == maxTotalMins &&
                        container.maxMins4Prep() == maxMins4Prep &&
                        container.minServings() == minServings &&
                        container.maxServings() == maxServings
        ));
    }

    @Test
    void searchByNameWithFiltering_WithNullFilterParameters_ShouldHandleNulls() {
        // Arrange
        var name = "pasta";
        var expectedResults = List.of(mock(RecipeResponseDto.class));
        when(searchService.searchByNameWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByNameWithFiltering(
                name, null, null, null, null, null
        );

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByNameWithFiltering(argThat(container ->
                container.name().equals(name) &&
                        container.maxMins4Cook() == null &&
                        container.maxTotalMins() == null &&
                        container.maxMins4Prep() == null &&
                        container.minServings() == null &&
                        container.maxServings() == null
        ));
    }

    @Test
    void searchByName_WithEmptyName_ShouldPassEmptyString() {
        // Arrange
        var name = "";
        var expectedResults = List.of(mock(RecipeResponseDto.class));
        when(searchService.searchByName(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByName(name);

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByName(argThat(container ->
                container.name().isEmpty()
        ));
    }

    @Test
    void searchByIngredients_WithEmptyList_ShouldPassEmptyList() {
        // Arrange
        var ingredients = List.<String>of();
        var expectedResults = List.of(mock(RecipeResponseDto.class));
        when(searchService.searchByIngredients(any(SearchContainer.class))).thenReturn(expectedResults);

        // Act
        var result = searchController.searchByIngredients(ingredients);

        // Assert
        assertEquals(expectedResults, result);
        verify(searchService).searchByIngredients(argThat(container ->
                container.ingredientsName().isEmpty()
        ));
    }
}